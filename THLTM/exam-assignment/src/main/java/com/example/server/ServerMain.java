package com.example.server;

import com.example.util.DatabaseManager;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static final int PORT = 9999;

    public static void main(String[] args) {
        DatabaseManager.initDB();

        ServerGUI gui = new ServerGUI();
        SwingUtilities.invokeLater(() -> gui.setVisible(true));

        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                gui.log("Server dang chay tai cong: " + PORT);
                gui.log("IP LAN: " + gui.getLanIP() + " - Client nhap IP nay de ket noi");
                gui.log("Cho client ket noi...");

                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    gui.log("Client ket noi tu: " + clientSocket.getInetAddress().getHostAddress());
                    new Thread(new ClientHandler(clientSocket, gui)).start();
                }
            } catch (IOException e) {
                gui.log("Loi server: " + e.getMessage());
            }
        }).start();
    }
}
