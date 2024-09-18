package org.example;

import java.io.BufferedOutputStream;

public class Main {
    public static void main(String[] args){
        final var server = new Server();
        // код инициализации сервера (из вашего предыдущего ДЗ)

        // добавление хендлеров (обработчиков)
        server.addHandler("GET", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Method Get, Path /messages");
            }
        });
        server.addHandler("POST", "/messages", new Handler() {
            public void handle(Request request, BufferedOutputStream responseStream) {
                System.out.println("Method POST, Path /messages");
            }
        });
        server.start();
    }
}