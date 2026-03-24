package lab4.server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerGUI extends JFrame {
    private JTextField portField = new JTextField("5000");
    private JButton startBtn = new JButton("Khởi động Server");
    private JTextArea logArea = new JTextArea();
    private boolean isRunning = false;

    public ServerGUI() {
        setTitle("TCP Server Manager - Database Lab");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Panel port
        JPanel northPanel = new JPanel(new FlowLayout());
        northPanel.add(new JLabel("Port:"));
        portField.setPreferredSize(new Dimension(80, 25));
        northPanel.add(portField);
        northPanel.add(startBtn);

        
        logArea.setEditable(false);
        logArea.setBackground(new Color(240, 240, 240));
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Hệ thống Log"));

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        
        startBtn.addActionListener(e -> {
            if (!isRunning) {
                int port = Integer.parseInt(portField.getText());
                startServer(port);
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void startServer(int port) {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                isRunning = true;
                updateLog("Server đã sẵn sàng tại cổng: " + port);
                
                
                SwingUtilities.invokeLater(() -> {
                    startBtn.setEnabled(false);
                    portField.setEditable(false);
                    startBtn.setText("Server đang chạy...");
                });

                while (isRunning) {
                    Socket clientSocket = serverSocket.accept();
                    updateLog("Kết nối mới từ: " + clientSocket.getInetAddress());
                    
                    
                    new ClientHandler(clientSocket, logArea).start();
                }
            } catch (IOException e) {
                updateLog("Lỗi Server: " + e.getMessage());
            }
        }).start();
    }

    private void updateLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("[" + new java.util.Date() + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}