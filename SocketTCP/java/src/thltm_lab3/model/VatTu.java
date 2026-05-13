package thltm_lab3.model;

import java.math.BigDecimal;

public class VatTu {
    private String maVT;
    private String tenVT;
    private String donViTinh;
    private BigDecimal donGia;
    private int soLuongTon;

    public VatTu() {
    }

    public VatTu(String maVT, String tenVT, String donViTinh, BigDecimal donGia, int soLuongTon) {
        this.maVT = maVT;
        this.tenVT = tenVT;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.soLuongTon = soLuongTon;
    }

    public String getMaVT() {
        return maVT;
    }

    public void setMaVT(String maVT) {
        this.maVT = maVT;
    }

    public String getTenVT() {
        return tenVT;
    }

    public void setTenVT(String tenVT) {
        this.tenVT = tenVT;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public int getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(int soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    @Override
    public String toString() {
        return "VatTu{" +
                "maVT='" + maVT + '\'' +
                ", tenVT='" + tenVT + '\'' +
                ", donViTinh='" + donViTinh + '\'' +
                ", donGia=" + donGia +
                ", soLuongTon=" + soLuongTon +
                '}';
    }
}
