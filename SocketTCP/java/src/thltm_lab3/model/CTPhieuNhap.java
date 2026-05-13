package thltm_lab3.model;

import java.math.BigDecimal;

public class CTPhieuNhap {
    private String maPN;
    private String maVT;
    private int soLuong;
    private BigDecimal donGiaNhap;

    public CTPhieuNhap() {
    }

    public CTPhieuNhap(String maPN, String maVT, int soLuong, BigDecimal donGiaNhap) {
        this.maPN = maPN;
        this.maVT = maVT;
        this.soLuong = soLuong;
        this.donGiaNhap = donGiaNhap;
    }

    public String getMaPN() {
        return maPN;
    }

    public void setMaPN(String maPN) {
        this.maPN = maPN;
    }

    public String getMaVT() {
        return maVT;
    }

    public void setMaVT(String maVT) {
        this.maVT = maVT;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGiaNhap() {
        return donGiaNhap;
    }

    public void setDonGiaNhap(BigDecimal donGiaNhap) {
        this.donGiaNhap = donGiaNhap;
    }

    @Override
    public String toString() {
        return "CTPhieuNhap{" +
                "maPN='" + maPN + '\'' +
                ", maVT='" + maVT + '\'' +
                ", soLuong=" + soLuong +
                ", donGiaNhap=" + donGiaNhap +
                '}';
    }
}
