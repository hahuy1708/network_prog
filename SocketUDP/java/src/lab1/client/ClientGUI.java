package lab1.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.BorderFactory;
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
    private final JTextField hostField = new JTextField("localhost", 12);
    private final JTextField portField = new JTextField("1234", 6);
    private final JTextField inputField = new JTextField();
    private final JTextArea resultArea = new JTextArea();
    private final JLabel statusLabel = new JLabel("Not connected");
    private final JButton connectButton = new JButton("Connect");
    private final JButton sendButton = new JButton("Send");

    private UDPClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Lab 1 UDP Client - String Processing");
        setSize(620, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel connectPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectPanel.setBorder(BorderFactory.createTitledBorder("Connection"));
        connectPanel.add(new JLabel("Server IP:"));
        connectPanel.add(hostField);
        connectPanel.add(new JLabel("Port:"));
        connectPanel.add(portField);
        connectPanel.add(connectButton);
        connectPanel.add(statusLabel);

        JPanel sendPanel = new JPanel(new BorderLayout(8, 0));
        sendPanel.setBorder(BorderFactory.createTitledBorder("Input"));
        sendPanel.add(inputField, BorderLayout.CENTER);
        sendPanel.add(sendButton, BorderLayout.EAST);

        resultArea.setEditable(false);

        add(connectPanel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);
        add(sendPanel, BorderLayout.SOUTH);

        sendButton.setEnabled(false);

        connectButton.addActionListener(e -> connectServer());
        sendButton.addActionListener(e -> sendData());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeClient();
            }
        });

        setVisible(true);
    }

    private void connectServer() {
        String host = hostField.getText().trim();
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port must be a number.");
            return;
        }

        connectButton.setEnabled(false);
        statusLabel.setText("Connecting...");

        new Thread(() -> {
            try {
                UDPClientSocket newSocket = new UDPClientSocket(host, port);
                closeClient();
                clientSocket = newSocket;

                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Connected (UDP)");
                    sendButton.setEnabled(true);
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> {
                    statusLabel.setText("Connect failed");
                    connectButton.setEnabled(true);
                    JOptionPane.showMessageDialog(this, "Cannot create UDP socket: " + e.getMessage());
                });
                return;
            }

            SwingUtilities.invokeLater(() -> connectButton.setEnabled(true));
        }, "lab1-udp-client-connect").start();
    }

    private void sendData() {
        String text = inputField.getText();
        if (text == null || text.trim().isEmpty()) {
            return;
        }

        if (clientSocket == null) {
            JOptionPane.showMessageDialog(this, "Please connect first.");
            return;
        }

        sendButton.setEnabled(false);
        new Thread(() -> {
            String response = clientSocket.sendAndReceive(text);
            SwingUtilities.invokeLater(() -> {
                resultArea.setText(response);
                sendButton.setEnabled(true);
            });
        }, "lab1-udp-client-send").start();
    }

    private void closeClient() {
        if (clientSocket != null) {
            clientSocket.close();
            clientSocket = null;
        }
    }
}
