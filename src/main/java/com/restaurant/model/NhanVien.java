package com.restaurant.model;

import com.restaurant.model.enums.CaLamViec;

import java.io.Serializable;
import java.util.Objects;

/**
 * Nhan vien nha hang.
 */
public class NhanVien implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maNV;
    private String tenNV;
    private CaLamViec caLamViec;
    private String sdt;
    private double luongTheoCa;

    public NhanVien() {
    }

    public NhanVien(String maNV, String tenNV, CaLamViec caLamViec, String sdt, double luongTheoCa) {
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.caLamViec = caLamViec;
        this.sdt = sdt;
        this.luongTheoCa = luongTheoCa;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenNV() {
        return tenNV;
    }

    public void setTenNV(String tenNV) {
        this.tenNV = tenNV;
    }

    public CaLamViec getCaLamViec() {
        return caLamViec;
    }

    public void setCaLamViec(CaLamViec caLamViec) {
        this.caLamViec = caLamViec;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public double getLuongTheoCa() {
        return luongTheoCa;
    }

    public void setLuongTheoCa(double luongTheoCa) {
        this.luongTheoCa = luongTheoCa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NhanVien nhanVien = (NhanVien) o;
        return Objects.equals(maNV, nhanVien.maNV);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNV);
    }

    @Override
    public String toString() {
        return tenNV;
    }
}
