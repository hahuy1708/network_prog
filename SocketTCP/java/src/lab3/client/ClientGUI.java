package lab3.client;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ClientGUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JTextArea chatArea = new JTextArea();
    private JTextField inputField = new JTextField();
    private ClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Lab 3: Chat Client");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        
        JPanel connectPage = new JPanel(new GridLayout(5, 1));
        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("5000");
        JButton connectBtn = new JButton("Kết nối vào Phòng Chat");
        connectPage.add(new JLabel("Địa chỉ Server:")); connectPage.add(hostField);
        connectPage.add(new JLabel("Cổng (Port):")); connectPage.add(portField);
        connectPage.add(connectBtn);

      
        JPanel chatPage = new JPanel(new BorderLayout());
        chatArea.setEditable(false);
        chatPage.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        chatPage.add(inputField, BorderLayout.SOUTH);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(chatPage, "CHAT");
        add(mainPanel);

        connectBtn.addActionListener(e -> {
            clientSocket = new ClientSocket(hostField.getText(), Integer.parseInt(portField.getText()), chatArea);
            if (clientSocket.connect()) {
                cardLayout.show(mainPanel, "CHAT");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể kết nối Server!");
            }
        });

        inputField.addActionListener(e -> {
        	String msg = inputField.getText();
        	if (!msg.isEmpty()) {
	            clientSocket.sendMessage(msg);
	            chatArea.append("[Tôi]: " + inputField.getText() + "\n");
	            inputField.setText("");
        	}
        });

        setVisible(true);
    }
}