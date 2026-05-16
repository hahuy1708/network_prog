package com.example.client;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.nio.file.Files;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ClientGUI extends JFrame {

    private JTextField txtHost;
    private JTextField txtPort;

    private JTextField txtM;
    private JTextField txtN;

    private JLabel lblCanBo;
    private JLabel lblPhong;
    private JButton btnChonCanBo;
    private JButton btnChonPhong;
    private File fileCanBo;
    private File filePhong;

    private JButton btnGui;

    private JTextArea txtLog;

    public ClientGUI() {
        setTitle("CLIENT - Phan Cong Can Bo Coi Thi");
        setSize(650, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(5, 5));

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.Y_AXIS));

        JPanel pKN = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pKN.setBorder(new TitledBorder("1. Ket noi Server"));
        pKN.add(new JLabel("Server IP:"));
        txtHost = new JTextField("localhost", 14);
        pKN.add(txtHost);
        pKN.add(new JLabel("Port:"));
        txtPort = new JTextField("9999", 6);
        pKN.add(txtPort);

        JPanel pMN = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pMN.setBorder(new TitledBorder("2. Tham so phan cong"));
        pMN.add(new JLabel("So giam thi (m):"));
        txtM = new JTextField("14", 6);
        pMN.add(txtM);
        pMN.add(new JLabel("So phong thi (n):"));
        txtN = new JTextField("6", 6);
        pMN.add(txtN);
        pMN.add(new JLabel("  (m >= 2, n >= 1, m va n khong vuot qua so dong trong file)"));

        JPanel pFile = new JPanel(new GridLayout(2, 3, 8, 8));
        pFile.setBorder(new TitledBorder("3. Chon file dau vao"));

        pFile.add(new JLabel("File can bo coi thi (.xlsx):"));
        lblCanBo = new JLabel("Chua chon");
        lblCanBo.setForeground(Color.GRAY);
        pFile.add(lblCanBo);
        btnChonCanBo = new JButton("Chon file...");
        pFile.add(btnChonCanBo);

        pFile.add(new JLabel("File danh sach phong (.xlsx):"));
        lblPhong = new JLabel("Chua chon");
        lblPhong.setForeground(Color.GRAY);
        pFile.add(lblPhong);
        btnChonPhong = new JButton("Chon file...");
        pFile.add(btnChonPhong);

        btnGui = new JButton("Gui len Server va Nhan ket qua");
        btnGui.setFont(new Font("Arial", Font.BOLD, 14));
        btnGui.setBackground(new Color(0, 120, 215));
        btnGui.setForeground(Color.WHITE);
        btnGui.setPreferredSize(new Dimension(350, 40));

        JPanel pBtn = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pBtn.setBorder(new TitledBorder("4. Thuc hien"));
        pBtn.add(btnGui);

        topPanel.add(pKN);
        topPanel.add(pMN);
        topPanel.add(pFile);
        topPanel.add(pBtn);

        txtLog = new JTextArea();
        txtLog.setEditable(false);
        txtLog.setFont(new Font("Consolas", Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(txtLog);
        scroll.setBorder(new TitledBorder("Log"));

        add(topPanel, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);

        btnChonCanBo.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chon file can bo coi thi");
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                fileCanBo = fc.getSelectedFile();
                lblCanBo.setText(fileCanBo.getName());
                lblCanBo.setForeground(new Color(0, 128, 0));
            }
        });

        btnChonPhong.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Chon file danh sach phong thi");
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                filePhong = fc.getSelectedFile();
                lblPhong.setText(filePhong.getName());
                lblPhong.setForeground(new Color(0, 128, 0));
            }
        });

        btnGui.addActionListener(e -> xuLyGuiServer());
    }

    private void xuLyGuiServer() {
        if (fileCanBo == null || filePhong == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui long chon du 2 file xlsx!", "Thieu file", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int m;
        int n;
        try {
            m = Integer.parseInt(txtM.getText().trim());
            n = Integer.parseInt(txtN.getText().trim());
            if (m < 2) {
                throw new NumberFormatException("m phai >= 2");
            }
            if (n < 1) {
                throw new NumberFormatException("n phai >= 1");
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "m phai la so nguyen >= 2\nn phai la so nguyen >= 1",
                    "Loi nhap lieu", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String host = txtHost.getText().trim();
        int port;
        try {
            port = Integer.parseInt(txtPort.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Port khong hop le!", "Loi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final int finalM = m;
        final int finalN = n;
        final int finalPort = port;
        btnGui.setEnabled(false);

        new Thread(() -> {
            log("Dang ket noi " + host + ":" + finalPort + " voi m=" + finalM + ", n=" + finalN + "...");
            try {
                NetworkClient client = new NetworkClient(host, finalPort);
                byte[][] results = client.guiVaNhan(finalM, finalN, fileCanBo, filePhong);
                String serverIp = client.getLastServerIp();
                if (serverIp != null && !serverIp.isBlank()) {
                    log("Server IP phan hoi: " + serverIp);
                }
                log("Nhan ve 2 file thanh cong. Chon noi luu...");

                JFileChooser fc = new JFileChooser();
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                fc.setDialogTitle("Chon thu muc luu file ket qua");
                int choice = fc.showSaveDialog(this);

                if (choice == JFileChooser.APPROVE_OPTION) {
                    File dir = fc.getSelectedFile();
                    File fPC = new File(dir, "DANHSACHPHANCONG.xlsx");
                    File fGS = new File(dir, "DANHSACHGIAMSAT.xlsx");
                    Files.write(fPC.toPath(), results[0]);
                    Files.write(fGS.toPath(), results[1]);
                    log("Luu thanh cong:");
                    log("  -> " + fPC.getAbsolutePath());
                    log("  -> " + fGS.getAbsolutePath());
                    JOptionPane.showMessageDialog(this,
                            "Hoan tat! Da luu 2 file ket qua.",
                            "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    log("Nguoi dung huy luu file.");
                }

            } catch (Exception ex) {
                log("Loi: " + ex.getMessage());
                JOptionPane.showMessageDialog(this,
                        "Ket noi that bai: " + ex.getMessage(),
                        "Loi ket noi", JOptionPane.ERROR_MESSAGE);
            } finally {
                SwingUtilities.invokeLater(() -> btnGui.setEnabled(true));
            }
        }).start();
    }

    public void log(String msg) {
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        SwingUtilities.invokeLater(() -> {
            txtLog.append("[" + time + "] " + msg + "\n");
            txtLog.setCaretPosition(txtLog.getDocument().getLength());
        });
    }
}
