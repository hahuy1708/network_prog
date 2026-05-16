package com.example.model;

public class PhanCong {
    private final int stt;
    private final CanBo canBo;
    private final PhongThi phong;
    private final int viTri;
    private final int soThuTuCa;

    public PhanCong(int stt, CanBo canBo, PhongThi phong, int viTri, int soThuTuCa) {
        this.stt = stt;
        this.canBo = canBo;
        this.phong = phong;
        this.viTri = viTri;
        this.soThuTuCa = soThuTuCa;
    }

    public int getStt() { return stt; }
    public CanBo getCanBo() { return canBo; }
    public PhongThi getPhong() { return phong; }
    public int getViTri() { return viTri; }
    public int getSoThuTuCa() { return soThuTuCa; }
}
