package com.restaurant.model;

import com.restaurant.model.enums.TrangThaiHoaDon;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Hoa don cho mot ban an.
 */
public class HoaDon implements Serializable {
    private static final long serialVersionUID = 1L;

    private String maHD;
    private String maBan;
    private String tenBan;
    private String maNV;
    private String tenNV;
    private List<ChiTietHoaDon> danhSachChiTiet;
    private LocalDateTime thoiGianTao;
    private LocalDateTime thoiGianThanhToan;
    private TrangThaiHoaDon trangThai;
    private double chietKhau;           // % chiet khau (0-100)
    private double tienKhachDua;
    private double tienThua;

    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
        this.trangThai = TrangThaiHoaDon.CHUA_THANH_TOAN;
    }

    public HoaDon(String maHD, String maBan, String tenBan, String maNV, String tenNV) {
        this();
        this.maHD = maHD;
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.maNV = maNV;
        this.tenNV = tenNV;
        this.thoiGianTao = LocalDateTime.now();
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
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

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    public LocalDateTime getThoiGianTao() {
        return thoiGianTao;
    }

    public void setThoiGianTao(LocalDateTime thoiGianTao) {
        this.thoiGianTao = thoiGianTao;
    }

    public LocalDateTime getThoiGianThanhToan() {
        return thoiGianThanhToan;
    }

    public void setThoiGianThanhToan(LocalDateTime thoiGianThanhToan) {
        this.thoiGianThanhToan = thoiGianThanhToan;
    }

    public TrangThaiHoaDon getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiHoaDon trangThai) {
        this.trangThai = trangThai;
    }

    public double getChietKhau() {
        return chietKhau;
    }

    public void setChietKhau(double chietKhau) {
        this.chietKhau = chietKhau;
    }

    public double getTienKhachDua() {
        return tienKhachDua;
    }

    public void setTienKhachDua(double tienKhachDua) {
        this.tienKhachDua = tienKhachDua;
    }

    public double getTienThua() {
        return tienThua;
    }

    public void setTienThua(double tienThua) {
        this.tienThua = tienThua;
    }

    /**
     * Them mon vao hoa don. Neu mon da co thi cong don so luong.
     * @param monAn
     * @param soLuong
     */
    public void themMon(MonAn monAn, int soLuong) {
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            if (ct.getMaMon().equals(monAn.getMaMon())) {
                ct.setSoLuong(ct.getSoLuong() + soLuong);
                return;
            }
        }
        danhSachChiTiet.add(new ChiTietHoaDon(monAn, soLuong));
    }

    /**
     * Xoa hoan toan mot mon khoi hoa don.
     * @param maMon
     */
    public void xoaMon(String maMon) {
        danhSachChiTiet.removeIf(ct -> ct.getMaMon().equals(maMon));
    }

    public double getTongTien() {
        double tong = 0;
        for (ChiTietHoaDon ct : danhSachChiTiet) {
            tong += ct.getThanhTien();
        }
        return tong;
    }

    /**
     * Tinh tong thanh toan sau chiet khau: Tong tien - (Tong tien * % chiet khau / 100).
     * @return 
     */
    public double getTongThanhToan() {
        double tong = getTongTien();
        return tong - (tong * chietKhau / 100.0);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HoaDon hoaDon = (HoaDon) o;
        return Objects.equals(maHD, hoaDon.maHD);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHD);
    }

    @Override
    public String toString() {
        return maHD;
    }
}
