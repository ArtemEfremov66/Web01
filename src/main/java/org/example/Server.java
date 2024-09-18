package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public ConcurrentHashMap<String, ConcurrentHashMap<String, Handler>> handlers = new ConcurrentHashMap<>();
    static ExecutorService executorService = Executors.newFixedThreadPool(64);
    public void start() {
            try (final ServerSocket serverSocket = new ServerSocket(9999)) {
                while(!serverSocket.isClosed()) {
                    final var socket = serverSocket.accept();
                    executorService.execute(new MonoThreadClientHandler(socket, handlers));
                    System.out.print("Connection accepted.");
                }
                executorService.shutdown();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void addHandler(String method, String path, Handler handler) {
        if (!handlers.containsKey(method)) {
            handlers.put(method, new ConcurrentHashMap<>());
        }
        handlers.get(method).put(path, handler);


    }
}
