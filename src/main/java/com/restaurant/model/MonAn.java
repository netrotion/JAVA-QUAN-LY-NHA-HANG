package com.restaurant.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Mon an trong thuc don.
 */
public class MonAn implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maMon;
    private String tenMon;
    private String danhMuc;
    private double donGia;

    public MonAn() {
    }

    public MonAn(String maMon, String tenMon, String danhMuc, double donGia) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.danhMuc = danhMuc;
        this.donGia = donGia;
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

    public String getDanhMuc() {
        return danhMuc;
    }

    public void setDanhMuc(String danhMuc) {
        this.danhMuc = danhMuc;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonAn monAn = (MonAn) o;
        return Objects.equals(maMon, monAn.maMon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maMon);
    }

    @Override
    public String toString() {
        return tenMon;
    }
}
