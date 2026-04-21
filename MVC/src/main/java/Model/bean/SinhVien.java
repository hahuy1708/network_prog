package Model.bean;

public class SinhVien {
    private String maSv;
    private String hoTen;
    private String lop;
    private float diemTb;

    public SinhVien() {}

    public SinhVien(String maSv, String hoTen, String lop, float diemTb) {
        this.maSv = maSv;
        this.hoTen = hoTen;
        this.lop = lop;
        this.diemTb = diemTb;
    }

    public String getMaSv() {
        return maSv;
    }

    public void setMaSv(String maSv) {
        this.maSv = maSv;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getLop() {
        return lop;
    }

    public void setLop(String lop) {
        this.lop = lop;
    }

    public float getDiemTb() {
        return diemTb;
    }

    public void setDiemTb(float diemTb) {
        this.diemTb = diemTb;
    }
}
