package com.example.model;

public class CanBo {
    private final int stt;
    private final String maGV;
    private final String hoTen;
    private final String ngaySinh;
    private final String donVi;

    public CanBo(int stt, String maGV, String hoTen, String ngaySinh, String donVi) {
        this.stt = stt;
        this.maGV = maGV;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.donVi = donVi;
    }

    public int getStt() { return stt; }
    public String getMaGV() { return maGV; }
    public String getHoTen() { return hoTen; }
    public String getNgaySinh() { return ngaySinh; }
    public String getDonVi() { return donVi; }
}
