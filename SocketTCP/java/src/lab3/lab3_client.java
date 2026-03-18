package lab3;

import java.io.*;
import java.net.*;

public class lab3_client {
    public static void main(String[] args) {
        String host = "localhost";
        int port = 5000;

        try {
            Socket socket = new Socket(host, port);
            System.out.println("--- DA KET NOI VOI SERVER ---");

            Thread receiveThread = new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        System.out.print("\r"); 
                        System.out.println(fromServer);
                        System.out.print("[Ban]: "); 
                    }
                } catch (IOException e) {
                    System.out.println("\nMat ket noi.");
                }
            });
            receiveThread.start();

            try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in))) {
                
                String userInput;
                while (true) {
                    System.out.print("[Ban]: ");
                    userInput = stdIn.readLine();
                    if (userInput == null || userInput.equalsIgnoreCase("exit")) break;
                    out.println(userInput);
                }
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("Khong the ket noi den Server.");
        }
    }
}