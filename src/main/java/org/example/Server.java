package org.example;
import java.io.*;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    static ExecutorService executorService = Executors.newFixedThreadPool(64);
    public void start() {
    try (final ServerSocket serverSocket = new ServerSocket(9999)) {
        final var socket = serverSocket.accept();
        executorService.execute(new MonoThreadClientHandler(socket));
        System.out.print("Connection accepted.");
        executorService.shutdown();
    } catch (IOException e) {
        e.printStackTrace();
    }
    }
}
