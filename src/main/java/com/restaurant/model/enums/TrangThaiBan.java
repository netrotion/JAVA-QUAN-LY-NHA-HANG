package com.restaurant.model.enums;

/**
 * Trang thai cua ban an.
 */
public enum TrangThaiBan {
    TRONG("Trống"),
    DANG_PHUC_VU("Đang phục vụ");

    private final String moTa;

    TrangThaiBan(String moTa) {
        this.moTa = moTa;
    }

    public String getMoTa() {
        return moTa;
    }

    @Override
    public String toString() {
        return moTa;
    }
}
