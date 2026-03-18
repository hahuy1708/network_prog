package lab3;

import java.io.*;
import java.net.*;
import java.util.*;

public class lab3_server {
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    public static void main(String[] args) throws IOException {
        int port = 5000;
        ServerSocket server = new ServerSocket(port);
        System.out.println("=== CHAT ROOM SERVER ===");
        System.out.println("Server dang chay tai cong: " + port);

        new Thread(() -> {
            try (BufferedReader consoleIn = new BufferedReader(new InputStreamReader(System.in))) {
                String serverMsg;
                while ((serverMsg = consoleIn.readLine()) != null) {
                    broadcast("[SERVER]: " + serverMsg, null);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        try {
            while (true) {
                new ClientHandler(server.accept()).start();
            }
        } finally {
            server.close();
        }
    }

    public static void broadcast(String message, PrintWriter senderOut) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                if (writer != senderOut) { 
                    writer.println(message);
                }
            }
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket s) { this.socket = s; }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) { clientWriters.add(out); }

                System.out.println("Co nguoi moi tham gia. Tong so: " + clientWriters.size());

                String message;
                while ((message = in.readLine()) != null) {
                    System.out.println("Client gui: " + message);
                    broadcast(message, this.out);
                }
            } catch (IOException e) {
            } finally {
                if (out != null) {
                    synchronized (clientWriters) { clientWriters.remove(out); }
                }
                System.out.println("Mot nguoi da thoat. Con lai: " + clientWriters.size());
                try { socket.close(); } catch (IOException e) {}
            }
        }
    }
}