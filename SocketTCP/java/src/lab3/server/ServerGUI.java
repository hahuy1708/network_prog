package lab3.server;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ServerGUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JTextArea chatArea = new JTextArea();
    private JTextField serverInput = new JTextField();
    public static Set<PrintWriter> clientWriters = new HashSet<>();

    public ServerGUI() {
        setTitle("Lab 3: Chat Server");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

       
        JPanel connectPage = new JPanel(new GridLayout(3, 1));
        JTextField portField = new JTextField("5000");
        JButton startBtn = new JButton("Khởi động Chat Server");
        connectPage.add(new JLabel("Nhập Port:", SwingConstants.CENTER));
        connectPage.add(portField);
        connectPage.add(startBtn);

        
        JPanel chatPage = new JPanel(new BorderLayout());
        chatArea.setEditable(false);
        chatPage.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel(" Server: "), BorderLayout.WEST);
        inputPanel.add(serverInput, BorderLayout.CENTER);
        chatPage.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(chatPage, "CHAT");
        add(mainPanel);

        startBtn.addActionListener(e -> {
            int port = Integer.parseInt(portField.getText());
            startServer(port);
            cardLayout.show(mainPanel, "CHAT");
        });

        
        serverInput.addActionListener(e -> {
            String msg = "[SERVER]: " + serverInput.getText();
            broadcast(msg,null);
            chatArea.append(msg + "\n");
            serverInput.setText("");
        });

        setVisible(true);
    }

    private void startServer(int port) {
        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)) {
                chatArea.append("Server đã chạy trên cổng " + port + "\n");
                while (true) {
                    Socket s = ss.accept();
                    new ClientHandler(s, chatArea).start();
                }
            } catch (IOException ex) { chatArea.append("Lỗi: " + ex.getMessage()); }
        }).start();
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
}