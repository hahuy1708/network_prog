package lab2.client;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class ClientGUI extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);

    private UDPClientSocket clientSocket;

    private final JTextField hostField = new JTextField("localhost");
    private final JTextField portField = new JTextField("1235");
    private final JTextField expressionField = new JTextField();
    private final JLabel resultLabel = new JLabel("Result will appear here", JLabel.CENTER);

    public ClientGUI() {
        setTitle("Lab 2 UDP Client - Calculator");
        setSize(520, 260);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel connectPage = new JPanel(new GridLayout(4, 2, 6, 6));
        JButton connectButton = new JButton("Connect");
        connectPage.add(new JLabel("Server IP:"));
        connectPage.add(hostField);
        connectPage.add(new JLabel("Port:"));
        connectPage.add(portField);
        connectPage.add(new JLabel());
        connectPage.add(connectButton);

        JPanel calculatorPage = new JPanel(new BorderLayout(8, 8));
        JPanel inputRow = new JPanel(new BorderLayout(6, 0));
        JButton calcButton = new JButton("Calculate");
        inputRow.add(expressionField, BorderLayout.CENTER);
        inputRow.add(calcButton, BorderLayout.EAST);

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(resultLabel);

        calculatorPage.add(new JLabel("Expression (+, -, *, /, parentheses):"), BorderLayout.NORTH);
        calculatorPage.add(inputRow, BorderLayout.CENTER);
        calculatorPage.add(center, BorderLayout.SOUTH);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(calculatorPage, "CALC");
        add(mainPanel);

        connectButton.addActionListener(e -> connect(connectButton));
        calcButton.addActionListener(e -> calculate(calcButton));

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeSocket();
            }
        });

        setVisible(true);
    }

    private void connect(JButton connectButton) {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port must be a number.");
            return;
        }

        String host = hostField.getText().trim();
        connectButton.setEnabled(false);

        new Thread(() -> {
            try {
                UDPClientSocket newSocket = new UDPClientSocket(host, port);
                closeSocket();
                clientSocket = newSocket;
                SwingUtilities.invokeLater(() -> cardLayout.show(mainPanel, "CALC"));
            } catch (IOException ex) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Connect failed: " + ex.getMessage()));
            } finally {
                SwingUtilities.invokeLater(() -> connectButton.setEnabled(true));
            }
        }, "lab2-udp-client-connect").start();
    }

    private void calculate(JButton calcButton) {
        String expression = expressionField.getText().trim();
        if (expression.isEmpty()) {
            return;
        }
        if (clientSocket == null) {
            JOptionPane.showMessageDialog(this, "Please connect first.");
            return;
        }

        calcButton.setEnabled(false);
        new Thread(() -> {
            String result = clientSocket.sendExpression(expression);
            SwingUtilities.invokeLater(() -> {
                resultLabel.setText(result);
                calcButton.setEnabled(true);
            });
        }, "lab2-udp-client-calc").start();
    }

    private void closeSocket() {
        if (clientSocket != null) {
            clientSocket.close();
            clientSocket = null;
        }
    }
}
