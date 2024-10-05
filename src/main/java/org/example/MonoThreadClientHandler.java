package org.example;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MonoThreadClientHandler implements Runnable {
    private static Socket clientDialog;
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers;

    public MonoThreadClientHandler(Socket socket, ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers) {
        MonoThreadClientHandler.clientDialog = socket;
        this.handlers = handlers;
    }

    @Override
    public void run() {
        try (final var in = new BufferedReader(new InputStreamReader(clientDialog.getInputStream()));
             final var out = new BufferedOutputStream(clientDialog.getOutputStream());
        ) {
            while (!clientDialog.isClosed()) {
                // read only request line for simplicity
                // must be in form GET /path HTTP/1.1
                final var requestLine = in.readLine();
                final String[] parts = requestLine.split(" ");

                if (parts.length != 3) {
                    // just close socket
                    continue;
                }
                final var method = parts[0];
                final String path = parts[1];
                List<NameValuePair> query = null;
                if (path.contains("?")) {
                    final var pathAndQuery = path.split("\\?");
                    final var queryString = pathAndQuery[1];
                    query = URLEncodedUtils.parse(queryString, null);
                }
                if (handlers.containsKey(method)) {
                    if (handlers.get(method).containsKey(path)) {
                        Request request = new Request(method, path, query, in);
                        handlers.get(method).get(path).handle(request, out);
                    }

                } else {
                    out.write((
                            "HTTP/1.1 404 Not Found\r\n" +
                                    "Content-Length: 0\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.flush();
                    continue;
                }

                final var filePath = Path.of(".", "public", path);
                final var mimeType = Files.probeContentType(filePath);

                // special case for classic
                if (path.equals("/classic.html")) {
                    final var template = Files.readString(filePath);
                    final var content = template.replace(
                            "{time}",
                            LocalDateTime.now().toString()
                    ).getBytes();
                    out.write((
                            "HTTP/1.1 200 OK\r\n" +
                                    "Content-Type: " + mimeType + "\r\n" +
                                    "Content-Length: " + content.length + "\r\n" +
                                    "Connection: close\r\n" +
                                    "\r\n"
                    ).getBytes());
                    out.write(content);
                    out.flush();
                    continue;
                }

                final var length = Files.size(filePath);
                out.write((
                        "HTTP/1.1 200 OK\r\n" +
                                "Content-Type: " + mimeType + "\r\n" +
                                "Content-Length: " + length + "\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                Files.copy(filePath, out);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
