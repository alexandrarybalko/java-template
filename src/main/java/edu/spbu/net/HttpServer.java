package edu.spbu.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Scanner;

public class HttpServer {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(80)) {
            System.out.println("Server created");
            while (true) {

                Socket socket = serverSocket.accept();
                System.out.println("Connection accepted");
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();

                Scanner scanner = new Scanner(inputStream);
                String str = scanner.nextLine();
                System.out.println("Request: " + str);
                String[] get_filename = str.split(" ");
                if (get_filename[0].equals("GET")) {
                    String fileName = get_filename[1].split("/")[1];
                    System.out.println("Opening " + fileName + "...\n");
                    File file = new File(fileName);
                    if (file.isFile()) {
                        String content = new String(Files.readAllBytes(Paths.get(fileName)));
                        String response = "HTTP/1.1 200 OK\n";
                        response += "Last-Modified: " + new Date(file.lastModified()) + "\n";
                        response += "Content-Length: " + file.length() + "\n";
                        response += "Content-Type: text/html\n";
                        response += "Connection: close\n";
                        response += "Server: Server\n\n";
                        System.out.println(response);
                        outputStream.write((response + content).getBytes());
                    }
                    else {
                        String response = "HTTP/1.1 404 Not Found\n";
                        response += "Date: " + new Date() + "\n";
                        response += "Content-Type: text/plain\n";
                        response += "Connection: close\n";
                        response += "Server: Server\n";
                        response += "File Not Found!";
                        outputStream.write(response.getBytes());
                        outputStream.flush();
                    }
                    outputStream.close();
                    inputStream.close();
                    socket.close();
                    System.out.println("Connection closed");
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
