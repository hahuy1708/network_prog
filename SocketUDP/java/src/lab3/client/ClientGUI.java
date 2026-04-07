package lab3.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private final JTextArea chatArea = new JTextArea();
    private final JTextField messageField = new JTextField();
    private UDPClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Lab 3 UDP Client - Chat Room");
        setSize(560, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel connectPage = new JPanel(new GridLayout(7, 1, 6, 6));
        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("5000");
        JTextField nameField = new JTextField("User" + (1000 + (int) (Math.random() * 9000)));
        JButton connectButton = new JButton("Connect to Chat Room");

        connectPage.add(new JLabel("Server IP:"));
        connectPage.add(hostField);
        connectPage.add(new JLabel("Port:"));
        connectPage.add(portField);
        connectPage.add(new JLabel("Display name:"));
        connectPage.add(nameField);
        connectPage.add(connectButton);

        JPanel chatPage = new JPanel(new BorderLayout(8, 8));
        chatArea.setEditable(false);
        chatPage.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPage.add(messageField, BorderLayout.SOUTH);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(chatPage, "CHAT");
        add(mainPanel);

        connectButton.addActionListener(e -> {
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Port must be a number.");
                return;
            }

            connectButton.setEnabled(false);
            String host = hostField.getText().trim();
            String username = nameField.getText().trim();

            new Thread(() -> {
                UDPClientSocket socket = new UDPClientSocket(host, port, username, this::appendMessage);
                if (socket.connect()) {
                    clientSocket = socket;
                    SwingUtilities.invokeLater(() -> {
                        cardLayout.show(mainPanel, "CHAT");
                        appendMessage("[System] Connected as " + username);
                        connectButton.setEnabled(true);
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        connectButton.setEnabled(true);
                        JOptionPane.showMessageDialog(this, "Cannot connect to server.");
                    });
                }
            }, "lab3-udp-client-connect").start();
        });

        messageField.addActionListener(e -> {
            String msg = messageField.getText().trim();
            if (msg.isEmpty() || clientSocket == null) {
                return;
            }
            clientSocket.sendMessage(msg);
            appendMessage("[Me]: " + msg);
            messageField.setText("");
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            }
        });

        setVisible(true);
    }

    private void appendMessage(String text) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(text + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}
