package lab4.client;

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
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class ClientGUI extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final JTable table = new JTable();
    private final JLabel statusLabel = new JLabel("Ready", JLabel.CENTER);

    private UDPClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Lab 4 UDP Client - Student Manager");
        setSize(760, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel connectPage = new JPanel(new GridLayout(3, 2, 8, 8));
        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("5001");
        JButton connectButton = new JButton("Connect");

        connectPage.add(new JLabel("Host:"));
        connectPage.add(hostField);
        connectPage.add(new JLabel("Port:"));
        connectPage.add(portField);
        connectPage.add(new JLabel());
        connectPage.add(connectButton);

        JPanel dataPage = new JPanel(new BorderLayout(8, 8));
        JButton fetchButton = new JButton("Fetch students");
        dataPage.add(new JScrollPane(table), BorderLayout.CENTER);
        dataPage.add(fetchButton, BorderLayout.SOUTH);
        dataPage.add(statusLabel, BorderLayout.NORTH);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(dataPage, "DATA");
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
            new Thread(() -> {
                try {
                    UDPClientSocket newSocket = new UDPClientSocket(host, port);
                    closeSocket();
                    clientSocket = newSocket;
                    SwingUtilities.invokeLater(() -> {
                        cardLayout.show(mainPanel, "DATA");
                        statusLabel.setText("Connected to " + host + ":" + port + " (UDP)");
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Connect failed: " + ex.getMessage()));
                } finally {
                    SwingUtilities.invokeLater(() -> connectButton.setEnabled(true));
                }
            }, "lab4-udp-client-connect").start();
        });

        fetchButton.addActionListener(e -> {
            if (clientSocket == null) {
                JOptionPane.showMessageDialog(this, "Please connect first.");
                return;
            }

            fetchButton.setEnabled(false);
            statusLabel.setText("Fetching data...");

            new Thread(() -> {
                UDPClientSocket.DataResponse response = clientSocket.getStudentData();
                SwingUtilities.invokeLater(() -> {
                    if (response.hasError()) {
                        statusLabel.setText("Error: " + response.getErrorMessage());
                        JOptionPane.showMessageDialog(this, response.getErrorMessage());
                    } else {
                        table.setModel(new DefaultTableModel(response.getRows(), response.getColumns()));
                        statusLabel.setText("Loaded " + response.getRows().length + " rows.");
                    }
                    fetchButton.setEnabled(true);
                });
            }, "lab4-udp-client-fetch").start();
        });

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeSocket();
            }
        });

        setVisible(true);
    }

    private void closeSocket() {
        if (clientSocket != null) {
            clientSocket.close();
            clientSocket = null;
        }
    }
}
