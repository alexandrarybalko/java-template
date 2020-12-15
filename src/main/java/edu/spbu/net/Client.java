package edu.spbu.net;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private final int port;
    private final String host;
    private final String URL;

    public Client(int port, String host, String URL) {
        this.port = port;
        this.host = host;
        this.URL = URL;
    }

    void start() {
        try (Socket socket = new Socket(this.host, this.port)) {
            System.out.println("Client connected to socket.");
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            String request = "GET " + URL + " HTTP/1.1\r\nHost: " + this.host + "\r\nConnection: close\r\n\r\n";
            System.out.println("Request: " + request);
            outputStream.write(request.getBytes());
            outputStream.flush();

            Scanner scanner = new Scanner(inputStream);
            while (scanner.hasNextLine())
                System.out.println(scanner.nextLine());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client(80, "localhost", "/ex.txt");
        client.start();
    }
}
