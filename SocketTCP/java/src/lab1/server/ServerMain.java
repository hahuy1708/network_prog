package lab1.server;
import java.net.*;
import java.io.*;

public class ServerMain {
    public static void main(String[] args) {
        int port = 1234;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server Lab 1 đang chạy tại cổng " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Chờ kết nối [5]
                new ClientHandler(clientSocket).start(); // Chạy đa tuyến [6]
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}