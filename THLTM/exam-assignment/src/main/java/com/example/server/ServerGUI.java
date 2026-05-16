package com.example.server;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;

public class ServerGUI extends JFrame {

    private JTextArea txtLog;
    private JLabel lblStatus;
    private String lanIP;

    public ServerGUI() {
        lanIP = detectLanIP();
        setTitle("SERVER - Phan Cong Can Bo Coi Thi | IP LAN: " + lanIP + " | Port: 9999");
        setSize(750, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        ipPanel.setBackground(new Color(0, 80, 160));
        JLabel lblIP = new JLabel("IP LAN: " + lanIP + " | Port: 9999");
        lblIP.setForeground(Color.WHITE);
        lblIP.setFont(new Font("Consolas", Font.BOLD, 16));
        ipPanel.add(lblIP);

        JLabel lblHint = new JLabel("Client nhap IP nay de ket noi");
        lblHint.setForeground(new Color(200, 220, 255));
        lblHint.setFont(new Font("Arial", Font.ITALIC, 12));
        ipPanel.add(lblHint);

        lblStatus = new JLabel("Status: dang khoi dong...", JLabel.LEFT);
        lblStatus.setForeground(new Color(0, 128, 0));
        lblStatus.setFont(new Font("Arial", Font.BOLD, 13));
        lblStatus.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(ipPanel, BorderLayout.NORTH);
        topPanel.add(lblStatus, BorderLayout.SOUTH);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setBackground(new Color(30, 30, 30));
        txtLog.setForeground(new Color(0, 255, 0));
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtLog.setMargin(new Insets(5, 10, 5, 10));

        JScrollPane scroll = new JScrollPane(txtLog);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(50, 50, 50));
        JLabel lblDB = new JLabel("DB: MySQL exam_db @ localhost:3306");
        lblDB.setForeground(Color.CYAN);
        lblDB.setFont(new Font("Consolas", Font.PLAIN, 11));
        infoPanel.add(lblDB);

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    private String detectLanIP() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                if (ni.isLoopback() || !ni.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String ip = addr.getHostAddress();
                    if (ip.startsWith("192.168.") || ip.startsWith("10.")) {
                        return ip;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Khong detect duoc IP LAN: " + e.getMessage());
        }
        return "localhost";
    }

    public void log(String msg) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + time + "] " + msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
            lblStatus.setText("Status: server dang chay - IP: " + lanIP + " - Port 9999");
        });
    }

    public String getLanIP() {
        return lanIP;
    }
}
