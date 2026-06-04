package com.restaurant.model;

import com.restaurant.model.enums.TrangThaiBan;

import java.io.Serializable;
import java.util.Objects;

/**
 * Ban an trong nha hang.
 */
public class Ban implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maBan;
    private String tenBan;
    private TrangThaiBan trangThai;

    public Ban() {
    }

    public Ban(String maBan, String tenBan, TrangThaiBan trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.trangThai = trangThai;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public TrangThaiBan getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiBan trangThai) {
        this.trangThai = trangThai;
    }

    public boolean isTrong() {
        return trangThai == TrangThaiBan.TRONG;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ban ban = (Ban) o;
        return Objects.equals(maBan, ban.maBan);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maBan);
    }

    @Override
    public String toString() {
        return tenBan;
    }
}
