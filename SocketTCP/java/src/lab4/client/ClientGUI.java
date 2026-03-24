package lab4.client;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ClientGUI extends JFrame {
    private CardLayout cl = new CardLayout();
    private JPanel mainPanel = new JPanel(cl);
    private JTable table = new JTable();
    private ClientSocket clientSocket;

    public ClientGUI() {
        setTitle("Student Manager");
        setSize(600, 400);

        JPanel login = new JPanel(new GridLayout(3, 2));
        JTextField hostF = new JTextField("localhost"), portF = new JTextField("5000");
        JButton btnConn = new JButton("Kết nối");
        login.add(new JLabel("Host:")); login.add(hostF);
        login.add(new JLabel("Port:")); login.add(portF);
        login.add(new JLabel()); login.add(btnConn);

        JPanel dataPage = new JPanel(new BorderLayout());
        JButton btnFetch = new JButton("Lấy dữ liệu SV");
        dataPage.add(new JScrollPane(table), BorderLayout.CENTER);
        dataPage.add(btnFetch, BorderLayout.SOUTH);

        mainPanel.add(login, "LOGIN");
        mainPanel.add(dataPage, "DATA");
        add(mainPanel);

        btnConn.addActionListener(e -> {
            new Thread(() -> {
                try {
                    clientSocket = new ClientSocket(hostF.getText(), Integer.parseInt(portF.getText()));
                    cl.show(mainPanel, "DATA");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Kết nối thất bại!");
                }
            }).start();
        });

        btnFetch.addActionListener(e -> {
            new Thread(() -> {
                try {
                    Object[] res = clientSocket.getStudentData();
                    SwingUtilities.invokeLater(() -> {
                        table.setModel(new DefaultTableModel((String[][])res[1], (String[])res[0]));
                    });
                } catch (Exception ex) { ex.printStackTrace(); }
            }).start();
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }
}