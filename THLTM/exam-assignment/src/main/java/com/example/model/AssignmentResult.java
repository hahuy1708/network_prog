package com.example.model;

import java.util.List;

public class AssignmentResult {
    private final List<PhanCong> danhSachPhanCong;
    private final List<CanBo> giamSatHanhLang;
    private final List<PhongThi> danhSachPhong;

    public AssignmentResult(
            List<PhanCong> danhSachPhanCong,
            List<CanBo> giamSatHanhLang,
            List<PhongThi> danhSachPhong) {
        this.danhSachPhanCong = danhSachPhanCong;
        this.giamSatHanhLang = giamSatHanhLang;
        this.danhSachPhong = danhSachPhong;
    }

    public List<PhanCong> getDanhSachPhanCong() { return danhSachPhanCong; }
    public List<CanBo> getGiamSatHanhLang() { return giamSatHanhLang; }
    public List<PhongThi> getDanhSachPhong() { return danhSachPhong; }
}
