package thltm_lab3.model;

import java.sql.Date;

public class PhieuNhap {
    private String maPN;
    private Date ngayNhap;
    private String maNCC;

    public PhieuNhap() {
    }

    public PhieuNhap(String maPN, Date ngayNhap, String maNCC) {
        this.maPN = maPN;
        this.ngayNhap = ngayNhap;
        this.maNCC = maNCC;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
    }

    public Date getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(Date ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public String getMaNCC() {
        return maNCC;
    }

    public void setMaNCC(String maNCC) {
        this.maNCC = maNCC;
    }

    @Override
    public String toString() {
        return "PhieuNhap{" +
                "maPN='" + maPN + '\'' +
                ", ngayNhap=" + ngayNhap +
                ", maNCC='" + maNCC + '\'' +
                '}';
    }
}
