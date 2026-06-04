package com.restaurant.model;

import com.restaurant.model.enums.VaiTro;

import java.io.Serializable;
import java.util.Objects;

/**
 * Tai khoan dang nhap he thong.
 */
public class TaiKhoan implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String password;
    private VaiTro vaiTro;
    private String maNV;
    private String tenHienThi;

    public TaiKhoan() {
    }

    public TaiKhoan(String username, String password, VaiTro vaiTro, String maNV, String tenHienThi) {
        this.username = username;
        this.password = password;
        this.vaiTro = vaiTro;
        this.maNV = maNV;
        this.tenHienThi = tenHienThi;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public VaiTro getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(VaiTro vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public String getTenHienThi() {
        return tenHienThi;
    }

    public void setTenHienThi(String tenHienThi) {
        this.tenHienThi = tenHienThi;
    }

    public boolean isAdmin() {
        return vaiTro == VaiTro.ADMIN;
    }

    /** Nhan vien phuc vu: tao va cap nhat order.
     * @return  */
    public boolean isNhanVien() {
        return vaiTro == VaiTro.NHAN_VIEN;
    }

    /** Thu ngan: thanh toan hoa don, giai phong ban.
     * @return  */
    public boolean isThuNgan() {
        return vaiTro == VaiTro.THU_NGAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaiKhoan taiKhoan = (TaiKhoan) o;
        return Objects.equals(username, taiKhoan.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username;
    }
}
