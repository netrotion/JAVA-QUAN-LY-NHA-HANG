package com.restaurant.service;

import com.restaurant.dao.NhanVienDAO;
import com.restaurant.model.NhanVien;
import com.restaurant.model.enums.CaLamViec;
import com.restaurant.util.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Nghiep vu quan ly nhan vien.
 */
public class NhanVienService {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    public List<NhanVien> getAll() {
        return nhanVienDAO.findAll();
    }

    public NhanVien getById(String maNV) {
        return nhanVienDAO.findById(maNV)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên: " + maNV));
    }

    public List<NhanVien> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String kw = keyword.toLowerCase();
        return getAll().stream()
                .filter(nv -> nv.getTenNV().toLowerCase().contains(kw)
                        || nv.getMaNV().toLowerCase().contains(kw)
                        || (nv.getSdt() != null && nv.getSdt().contains(kw)))
                .collect(Collectors.toList());
    }

    public NhanVien them(String tenNV, CaLamViec caLamViec, String sdt, double luongTheoCa) {
        validate(tenNV, sdt, luongTheoCa);
        List<String> ids = getAll().stream().map(NhanVien::getMaNV).collect(Collectors.toList());
        String maMoi = IDGenerator.generate("NV", ids, 3);
        NhanVien nhanVien = new NhanVien(maMoi, tenNV.trim(), caLamViec, sdt.trim(), luongTheoCa);
        nhanVienDAO.add(nhanVien);
        return nhanVien;
    }

    public void capNhat(String maNV, String tenNV, CaLamViec caLamViec, String sdt, double luongTheoCa) {
        validate(tenNV, sdt, luongTheoCa);
        NhanVien nhanVien = getById(maNV);
        nhanVien.setTenNV(tenNV.trim());
        nhanVien.setCaLamViec(caLamViec);
        nhanVien.setSdt(sdt.trim());
        nhanVien.setLuongTheoCa(luongTheoCa);
        nhanVienDAO.update(nhanVien);
    }

    public void xoa(String maNV) {
        nhanVienDAO.delete(maNV);
    }

    private void validate(String tenNV, String sdt, double luongTheoCa) {
        if (tenNV == null || tenNV.isBlank()) {
            throw new IllegalArgumentException("Tên nhân viên không được để trống.");
        }
        if (sdt == null || sdt.isBlank()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống.");
        }
        if (!sdt.trim().matches("\\d{9,11}")) {
            throw new IllegalArgumentException("Số điện thoại phải gồm 9-11 chữ số.");
        }
        if (luongTheoCa <= 0) {
            throw new IllegalArgumentException("Lương theo ca phải là số dương (> 0).");
        }
    }
}
