package Model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Model.bean.SinhVien;
import Model.DB.DBConnection;

public class SinhVienDAO {

    public SinhVien getSVByID(String maSv) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT ma_sv, ho_ten, lop, diem_tb FROM sv WHERE ma_sv = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maSv);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SinhVien(
                    rs.getString("ma_sv"),
                    rs.getString("ho_ten"),
                    rs.getString("lop"),
                    rs.getFloat("diem_tb")
                );
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public ArrayList<SinhVien> getAllSV() {
        ArrayList<SinhVien> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT ma_sv, ho_ten, lop, diem_tb FROM sv ORDER BY ma_sv";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                SinhVien sv = new SinhVien(
                    rs.getString("ma_sv"),
                    rs.getString("ho_ten"),
                    rs.getString("lop"),
                    rs.getFloat("diem_tb")
                );
                list.add(sv);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public void insertSV(SinhVien sv) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "INSERT INTO sv(ma_sv, ho_ten, lop, diem_tb) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sv.getMaSv());
            stmt.setString(2, sv.getHoTen());
            stmt.setString(3, sv.getLop());
            stmt.setFloat(4, sv.getDiemTb());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateSV(SinhVien sv) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE sv SET ho_ten = ?, lop = ?, diem_tb = ? WHERE ma_sv = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, sv.getHoTen());
            stmt.setString(2, sv.getLop());
            stmt.setFloat(3, sv.getDiemTb());
            stmt.setString(4, sv.getMaSv());
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteSV(String maSv) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM sv WHERE ma_sv = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, maSv);
            stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int deleteAllSV(ArrayList<String> listMaSv) {
        if (listMaSv == null || listMaSv.isEmpty()) return 0;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listMaSv.size(); i++) {
            sb.append("?,");
        }
        sb.setLength(sb.length() - 1);
        String sql = "DELETE FROM sv WHERE ma_sv IN (" + sb.toString() + ")";

        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (int i = 0; i < listMaSv.size(); i++) {
                stmt.setString(i + 1, listMaSv.get(i));
            }
            return stmt.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

}
