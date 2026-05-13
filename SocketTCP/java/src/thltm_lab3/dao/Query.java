package thltm_lab3.dao;

import thltm_lab3.db.DatabaseConnect;
import thltm_lab3.model.CTPhieuNhap;
import thltm_lab3.model.NhaCungCap;
import thltm_lab3.model.PhieuNhap;
import thltm_lab3.model.VatTu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Query {
    public List<VatTu> getAllVatTu() {
        List<VatTu> list = new ArrayList<>();
        String sql = "SELECT MaVT, TenVT, DonViTinh, DonGia, SoLuongTon FROM VatTu ORDER BY MaVT";

        try (Connection conn = DatabaseConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                VatTu vatTu = new VatTu(
                        rs.getString("MaVT"),
                        rs.getString("TenVT"),
                        rs.getString("DonViTinh"),
                        rs.getBigDecimal("DonGia"),
                        rs.getInt("SoLuongTon")
                );
                list.add(vatTu);
            }
        } catch (SQLException ex) {
            System.err.println("Loi truy van VatTu: " + ex.getMessage());
        }

        return list;
    }

    public List<NhaCungCap> getAllNhaCungCap() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT MaNCC, TenNCC, DiaChi, DienThoai FROM NhaCungCap ORDER BY MaNCC";

        try (Connection conn = DatabaseConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                NhaCungCap ncc = new NhaCungCap(
                        rs.getString("MaNCC"),
                        rs.getString("TenNCC"),
                        rs.getString("DiaChi"),
                        rs.getString("DienThoai")
                );
                list.add(ncc);
            }
        } catch (SQLException ex) {
            System.err.println("Loi truy van NhaCungCap: " + ex.getMessage());
        }

        return list;
    }

    public List<PhieuNhap> getAllPhieuNhap() {
        List<PhieuNhap> list = new ArrayList<>();
        String sql = "SELECT MaPN, NgayNhap, MaNCC FROM PhieuNhap ORDER BY MaPN";

        try (Connection conn = DatabaseConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                PhieuNhap pn = new PhieuNhap(
                        rs.getString("MaPN"),
                        rs.getDate("NgayNhap"),
                        rs.getString("MaNCC")
                );
                list.add(pn);
            }
        } catch (SQLException ex) {
            System.err.println("Loi truy van PhieuNhap: " + ex.getMessage());
        }

        return list;
    }

    public List<CTPhieuNhap> getAllCTPhieuNhap() {
        List<CTPhieuNhap> list = new ArrayList<>();
        String sql = "SELECT MaPN, MaVT, SoLuong, DonGiaNhap FROM CTPhieuNhap ORDER BY MaPN, MaVT";

        try (Connection conn = DatabaseConnect.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CTPhieuNhap ct = new CTPhieuNhap(
                        rs.getString("MaPN"),
                        rs.getString("MaVT"),
                        rs.getInt("SoLuong"),
                        rs.getBigDecimal("DonGiaNhap")
                );
                list.add(ct);
            }
        } catch (SQLException ex) {
            System.err.println("Loi truy van CTPhieuNhap: " + ex.getMessage());
        }

        return list;
    }
}
