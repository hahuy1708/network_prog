package lab1.client;
import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private CardLayout cl = new CardLayout();
    private JPanel mainPanel = new JPanel(cl);
    
    // Components connect
    private JTextField hostField = new JTextField("localhost");
    private JTextField portField = new JTextField("1234");
    
    // Components string
    private JTextField inputField = new JTextField(20);
    private JTextArea resultArea = new JTextArea(10, 30);
    private JButton sendButton = new JButton("Gửi Server");
    
    private ClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Lab 1: String Processing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // CONNECT
        JPanel connectPage = new JPanel(new GridLayout(3, 2, 10, 10));
        connectPage.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JButton connectBtn = new JButton("Kết nối");
        
        connectPage.add(new JLabel("Server IP:")); connectPage.add(hostField);
        connectPage.add(new JLabel("Port:")); connectPage.add(portField);
        connectPage.add(new JLabel("")); connectPage.add(connectBtn);
        
        // STRING PROCESSING
        JPanel stringPage = new JPanel(new BorderLayout(10, 10));
        stringPage.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JLabel("Nhập chuỗi: "), BorderLayout.NORTH);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        
        resultArea.setEditable(false);
        stringPage.add(inputPanel, BorderLayout.NORTH);
        stringPage.add(new JScrollPane(resultArea), BorderLayout.CENTER);

        mainPanel.add(connectPage, "CONNECT_CARD");
        mainPanel.add(stringPage, "STRING_CARD");
        
        add(mainPanel);

        connectBtn.addActionListener(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                
                clientSocket = new ClientSocket(host, port);
                
                
                cl.show(mainPanel, "STRING_CARD");
                pack(); // 
                setLocationRelativeTo(null);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage());
            }
        });
        
        
        sendButton.addActionListener(e -> {
            String text = inputField.getText();
            if(!text.isEmpty()){
                String res = clientSocket.sendRequest(text);
                resultArea.setText(res);
            }
        });

        pack();
        setLocationRelativeTo(null); 
        setVisible(true);
    }
}