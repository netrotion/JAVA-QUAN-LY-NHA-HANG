package com.restaurant.model.enums;

/**
 * Vai tro tai khoan dang nhap.
 * Tuong ung 3 tac nhan trong he thong:
 *  - ADMIN      : Quan ly (quan ly du lieu nen, tra cuu/bao cao, tao/cap nhat order).
 *  - NHAN_VIEN  : Nhan vien phuc vu (tao/cap nhat order).
 *  - THU_NGAN   : Thu ngan (thanh toan hoa don, giai phong ban).
 */
public enum VaiTro {
    ADMIN("Quản lý"),
    NHAN_VIEN("Nhân viên phục vụ"),
    THU_NGAN("Thu ngân");

    private final String moTa;

    VaiTro(String moTa) {
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
