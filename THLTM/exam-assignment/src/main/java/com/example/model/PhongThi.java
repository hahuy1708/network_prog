package com.example.model;

public class PhongThi {
    private final int stt;
    private final String maPhong;
    private final String diaDiem;

    public PhongThi(int stt, String maPhong, String diaDiem) {
        this.stt = stt;
        this.maPhong = maPhong;
        this.diaDiem = diaDiem;
    }

    public int getStt() { return stt; }
    public String getMaPhong() { return maPhong; }
    public String getDiaDiem() { return diaDiem; }
}
