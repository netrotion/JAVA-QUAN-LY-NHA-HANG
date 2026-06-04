package com.restaurant.model;

import java.io.Serializable;

/**
 * Mot dong chi tiet trong hoa don (1 mon an + so luong).
 */
public class ChiTietHoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maMon;
    private String tenMon;
    private double donGia;
    private int soLuong;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(String maMon, String tenMon, double donGia, int soLuong) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.donGia = donGia;
        this.soLuong = soLuong;
    }

    public ChiTietHoaDon(MonAn monAn, int soLuong) {
        this(monAn.getMaMon(), monAn.getTenMon(), monAn.getDonGia(), soLuong);
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public double getThanhTien() {
        return donGia * soLuong;
    }
}
