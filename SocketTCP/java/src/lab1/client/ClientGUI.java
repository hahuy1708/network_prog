package lab1.client;
import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private JTextField inputField = new JTextField(20);
    private JTextArea resultArea = new JTextArea(10, 30);
    private JButton sendButton = new JButton("Gửi Server");

    public ClientGUI() {
        setTitle("Lab 1: String Processing");
        setLayout(new FlowLayout());
        add(new JLabel("Nhập chuỗi:"));
        add(inputField);
        add(sendButton);
        add(new JScrollPane(resultArea));

        sendButton.addActionListener(e -> {
            ClientSocket socket = new ClientSocket();
            String res = socket.sendRequest(inputField.getText());
            resultArea.setText(res);
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }
}