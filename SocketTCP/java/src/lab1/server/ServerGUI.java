package lab1.server;
import javax.swing.*;
import java.awt.*;
import java.net.*;

public class ServerGUI extends JFrame {
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainPanel = new JPanel(cardLayout);
    private JTextArea logArea = new JTextArea(10, 30);

    public ServerGUI() {
        setTitle("Lab 1 Server: String");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // TRANG 1: Mở cổng kết nối
        JPanel connectPage = new JPanel(new GridLayout(3, 1, 10, 10));
        JTextField portField = new JTextField("1234");
        JButton startBtn = new JButton("Bắt đầu lắng nghe (TCP)");
        connectPage.add(new JLabel("Nhập cổng Server:", SwingConstants.CENTER));
        connectPage.add(portField);
        connectPage.add(startBtn);

        // TRANG 2: Nhật ký hoạt động
        JPanel statusPage = new JPanel(new BorderLayout());
        logArea.setEditable(false);
        statusPage.add(new JLabel("--- Server Log ---", SwingConstants.CENTER), BorderLayout.NORTH);
        statusPage.add(new JScrollPane(logArea), BorderLayout.CENTER);

        mainPanel.add(connectPage, "CONNECT");
        mainPanel.add(statusPage, "STATUS");
        add(mainPanel);

        startBtn.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText());
                startServer(port);
                cardLayout.show(mainPanel, "STATUS");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Cổng phải là số!");
            }
        });
        setVisible(true);
    }

    private void startServer(int port) {
        new Thread(() -> {
            try (ServerSocket ss = new ServerSocket(port)) { 
                logArea.append("Server đã mở tại cổng " + port + "\n");
                while (true) {
                    Socket s = ss.accept();
                    logArea.append("Kết nối mới: " + s.getInetAddress() + "\n");
                    new ClientHandler(s, logArea).start(); 
                }
            } catch (Exception ex) {
                logArea.append("Lỗi Server: " + ex.getMessage() + "\n");
            }
        }).start();
    }
}