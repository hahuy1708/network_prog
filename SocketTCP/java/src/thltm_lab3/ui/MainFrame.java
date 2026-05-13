package thltm_lab3.ui;

import thltm_lab3.dao.Query;
import thltm_lab3.db.DatabaseConnect;
import thltm_lab3.model.CTPhieuNhap;
import thltm_lab3.model.NhaCungCap;
import thltm_lab3.model.PhieuNhap;
import thltm_lab3.model.VatTu;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;

public class MainFrame extends JFrame {
    private final Query query;
    private final JTable table;
    private final DefaultTableModel tableModel;
    private final JLabel statusLabel;
    private final JButton btnVatTu;
    private final JButton btnNhaCungCap;
    private final JButton btnPhieuNhap;
    private final JButton btnCTPhieuNhap;
    private final JButton btnClear;
    private final JButton btnExit;

    public MainFrame() {
        this.query = new Query();
        this.tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        this.table = new JTable(tableModel);
        this.statusLabel = new JLabel("Status: checking connection...", SwingConstants.RIGHT);
        this.btnVatTu = new JButton("Xem Vat Tu");
        this.btnNhaCungCap = new JButton("Xem Nha Cung Cap");
        this.btnPhieuNhap = new JButton("Xem Phieu Nhap");
        this.btnCTPhieuNhap = new JButton("Xem CT Phieu Nhap");
        this.btnClear = new JButton("Clear");
        this.btnExit = new JButton("Thoat");

        initUi();
        updateConnectionStatus();
        registerActions();
    }

    private void initUi() {
        setTitle("Quan Ly Vat Tu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 520));
        setLocationRelativeTo(null);

        JLabel header = new JLabel("Quan ly vat tu - JDBC", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 18));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(header, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actionPanel.add(btnVatTu);
        actionPanel.add(btnNhaCungCap);
        actionPanel.add(btnPhieuNhap);
        actionPanel.add(btnCTPhieuNhap);
        actionPanel.add(btnClear);
        actionPanel.add(btnExit);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(headerPanel, BorderLayout.NORTH);
        northPanel.add(actionPanel, BorderLayout.SOUTH);

        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(table);

        add(northPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void registerActions() {
        btnVatTu.addActionListener(event -> loadVatTu());
        btnNhaCungCap.addActionListener(event -> loadNhaCungCap());
        btnPhieuNhap.addActionListener(event -> loadPhieuNhap());
        btnCTPhieuNhap.addActionListener(event -> loadCTPhieuNhap());
        btnClear.addActionListener(event -> clearTable());
        btnExit.addActionListener(event -> dispose());
    }

    private void updateConnectionStatus() {
        try (Connection conn = DatabaseConnect.getConnection()) {
            statusLabel.setText("Status: connected");
        } catch (SQLException ex) {
            statusLabel.setText("Status: connection failed");
            setButtonsEnabled(false);
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    this,
                    "Connection failed: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            ));
        }
    }

    private void loadVatTu() {
        List<VatTu> list = query.getAllVatTu();
        String[] columns = {"MaVT", "TenVT", "DVT", "DonGia", "SLTon"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (VatTu vt : list) {
            tableModel.addRow(new Object[] {
                    safe(vt.getMaVT()),
                    safe(vt.getTenVT()),
                    safe(vt.getDonViTinh()),
                    formatMoney(vt.getDonGia()),
                    vt.getSoLuongTon()
            });
        }

        statusLabel.setText("Status: loaded " + list.size() + " vat tu");
    }

    private void loadNhaCungCap() {
        List<NhaCungCap> list = query.getAllNhaCungCap();
        String[] columns = {"MaNCC", "TenNCC", "DiaChi", "DienThoai"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (NhaCungCap ncc : list) {
            tableModel.addRow(new Object[] {
                    safe(ncc.getMaNCC()),
                    safe(ncc.getTenNCC()),
                    safe(ncc.getDiaChi()),
                    safe(ncc.getDienThoai())
            });
        }

        statusLabel.setText("Status: loaded " + list.size() + " nha cung cap");
    }

    private void loadPhieuNhap() {
        List<PhieuNhap> list = query.getAllPhieuNhap();
        String[] columns = {"MaPN", "NgayNhap", "MaNCC"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (PhieuNhap pn : list) {
            tableModel.addRow(new Object[] {
                    safe(pn.getMaPN()),
                    formatDate(pn.getNgayNhap()),
                    safe(pn.getMaNCC())
            });
        }

        statusLabel.setText("Status: loaded " + list.size() + " phieu nhap");
    }

    private void loadCTPhieuNhap() {
        List<CTPhieuNhap> list = query.getAllCTPhieuNhap();
        String[] columns = {"MaPN", "MaVT", "SoLuong", "DonGiaNhap"};
        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        for (CTPhieuNhap ct : list) {
            tableModel.addRow(new Object[] {
                    safe(ct.getMaPN()),
                    safe(ct.getMaVT()),
                    ct.getSoLuong(),
                    formatMoney(ct.getDonGiaNhap())
            });
        }

        statusLabel.setText("Status: loaded " + list.size() + " ct phieu nhap");
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        statusLabel.setText("Status: table cleared");
    }

    private void setButtonsEnabled(boolean enabled) {
        btnVatTu.setEnabled(enabled);
        btnNhaCungCap.setEnabled(enabled);
        btnPhieuNhap.setEnabled(enabled);
        btnCTPhieuNhap.setEnabled(enabled);
        btnClear.setEnabled(enabled);
    }

    private String formatMoney(BigDecimal value) {
        if (value == null) {
            return "";
        }
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value);
    }

    private String formatDate(Date value) {
        return value == null ? "" : value.toString();
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
