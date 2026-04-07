package lab3.server;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class ServerGUI extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JTextArea chatArea = new JTextArea();
    private final JTextField serverInput = new JTextField();

    private UDPHandler handler;

    public ServerGUI() {
        setTitle("Lab 3 UDP Server - Chat Room");
        setSize(600, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel connectPage = new JPanel(new GridLayout(3, 1, 8, 8));
        JTextField portField = new JTextField("5000");
        JButton startButton = new JButton("Start Chat Server (UDP)");
        connectPage.add(new JLabel("Input port", SwingConstants.CENTER));
        connectPage.add(portField);
        connectPage.add(startButton);

        JPanel chatPage = new JPanel(new BorderLayout(8, 8));
        chatArea.setEditable(false);
        JPanel inputPanel = new JPanel(new BorderLayout(6, 0));
        JButton sendButton = new JButton("Broadcast");
        inputPanel.add(new JLabel("Server:"), BorderLayout.WEST);
        inputPanel.add(serverInput, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        chatPage.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPage.add(inputPanel, BorderLayout.SOUTH);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(chatPage, "CHAT");
        add(mainPanel);

        startButton.addActionListener(e -> {
            int port;
            try {
                port = Integer.parseInt(portField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Port must be a number.");
                return;
            }

            handler = new UDPHandler(this::appendMessage);
            handler.start(port);
            cardLayout.show(mainPanel, "CHAT");
        });

        sendButton.addActionListener(e -> sendServerMessage());
        serverInput.addActionListener(e -> sendServerMessage());

        setVisible(true);
    }

    private void sendServerMessage() {
        String text = serverInput.getText().trim();
        if (text.isEmpty() || handler == null) {
            return;
        }
        handler.broadcastFromServer("[SERVER]: " + text);
        appendMessage("[SERVER]: " + text);
        serverInput.setText("");
    }

    private void appendMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        });
    }
}
