package lab1.server;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ServerGUI extends JFrame {
    private final JTextField portField = new JTextField("1234", 8);
    private final JButton startButton = new JButton("Start UDP Server");
    private final JTextArea logArea = new JTextArea();

    public ServerGUI() {
        setTitle("Lab 1 UDP Server - String Processing");
        setSize(520, 380);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Port:"));
        topPanel.add(portField);
        topPanel.add(startButton);

        logArea.setEditable(false);
        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(logArea), BorderLayout.CENTER);

        startButton.addActionListener(e -> startServer());

        setVisible(true);
    }

    private void startServer() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port must be a number.");
            return;
        }

        startButton.setEnabled(false);
        portField.setEditable(false);
        UDPHandler.startServer(port, this::appendLog);
    }

    private void appendLog(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
