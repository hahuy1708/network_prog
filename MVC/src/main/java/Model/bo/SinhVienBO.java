package Model.bo;

import java.util.ArrayList;
import Model.bean.SinhVien;
import Model.dao.SinhVienDAO;

public class SinhVienBO {
    SinhVienDAO dao = new SinhVienDAO();

    public ArrayList<SinhVien> getAllSinhVien() {
        return dao.getAllSV();
    }

    public void insertSinhVien(SinhVien sv) {
        dao.insertSV(sv);
    }

    public void updateSinhVien(SinhVien sv) {
        dao.updateSV(sv);
    }

    public SinhVien getSinhVienByID(String maSv) {
        return dao.getSVByID(maSv);
    }

    public void deleteSinhVien(String maSv) {
        dao.deleteSV(maSv);
    }

    public int deleteAllSinhVien(ArrayList<String> listMaSv) {
        return dao.deleteAllSV(listMaSv);
    }
    
}
