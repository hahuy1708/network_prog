package thltm_lab1.bai3;
import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    // Danh sach luu tru luong xuat cua tat ca client dang ket noi
    private static Set<PrintWriter> clientWriters = new HashSet<>();

    private static void broadcastFromServer(String msg) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println("Server: " + msg);
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Chat Room san sang tai cong " + port);

            Thread serverInputThread = new Thread(() -> {
                try (BufferedReader kb = new BufferedReader(new InputStreamReader(System.in))) {
                    String line;
                    while ((line = kb.readLine()) != null) {
                        if (line.equalsIgnoreCase("exit")) {
                            break;
                        }
                        broadcastFromServer(line);
                    }
                } catch (IOException e) {
                }
            });
            serverInputThread.setDaemon(true);
            serverInputThread.start();

            while (true) {
                new Handler(serverSocket.accept()).start(); // Chap nhan nhieu ket noi [14]
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    private static class Handler extends Thread {
        private Socket socket;
        private PrintWriter out;

        public Handler(Socket socket) { this.socket = socket; }

        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);
                synchronized (clientWriters) { clientWriters.add(out); }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.equalsIgnoreCase("exit")) break;
                    System.out.println("Nhan: " + message);
                    broadcast(message, out); // Chuyen tiep tin nhan
                }
            } catch (IOException e) {
            } finally {
                if (out != null) { synchronized (clientWriters) { clientWriters.remove(out); } }
                try { socket.close(); } catch (IOException e) { }
            }
        }

        private void broadcast(String msg, PrintWriter sender) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    if (writer != sender) writer.println("Ban be: " + msg);
                }
            }
        }
    }
}