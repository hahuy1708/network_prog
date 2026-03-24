package lab2.client;
import javax.swing.*;
import java.awt.*;

public class ClientGUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private ClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Lab 2 Client: TCP Calculator");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TRANG 1: Thiết lập kết nối
        JPanel connectPage = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField hostField = new JTextField("localhost");
        JTextField portField = new JTextField("1234");
        JButton connectBtn = new JButton("Kết nối");
        connectPage.add(new JLabel("Server IP:")); connectPage.add(hostField);
        connectPage.add(new JLabel("Port:")); connectPage.add(portField);
        connectPage.add(new JLabel("")); connectPage.add(connectBtn);

        // TRANG 2: Tính toán
        JPanel calcPage = new JPanel(new BorderLayout(10, 10));
        JTextField inputField = new JTextField();
        JLabel resultLabel = new JLabel("Kết quả sẽ hiển thị ở đây", SwingConstants.CENTER);
        JButton calcBtn = new JButton("Tính toán");
        calcPage.add(new JLabel("Nhập biểu thức (VD: 5+13-(12-4*6)):"), BorderLayout.NORTH);
        calcPage.add(inputField, BorderLayout.CENTER);
        calcPage.add(calcBtn, BorderLayout.SOUTH);
        calcPage.add(resultLabel, BorderLayout.NORTH); // Re-layout simple for demo

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(calcPage, "CALC");
        add(mainPanel);

        connectBtn.addActionListener(e -> {
            clientSocket = new ClientSocket(hostField.getText(), Integer.parseInt(portField.getText()));
            cardLayout.show(mainPanel, "CALC");
        });

        calcBtn.addActionListener(e -> {
            String res = clientSocket.sendExpression(inputField.getText());
            JOptionPane.showMessageDialog(this, res);
        });

        setVisible(true);
    }
}