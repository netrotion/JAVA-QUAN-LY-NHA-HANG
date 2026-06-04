package com.restaurant.model.enums;

/**
 * Ca lam viec cua nhan vien.
 */
public enum CaLamViec {
    SANG("Ca sáng"),
    CHIEU("Ca chiều"),
    TOI("Ca tối"),
    GAY("Ca gay");

    private final String moTa;

    CaLamViec(String moTa) {
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
