package com.restaurant.service;

import com.restaurant.dao.MonAnDAO;
import com.restaurant.model.MonAn;
import com.restaurant.util.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Nghiep vu quan ly thuc don (mon an).
 */
public class MonAnService {

    private final MonAnDAO monAnDAO = new MonAnDAO();

    public List<MonAn> getAll() {
        return monAnDAO.findAll();
    }

    /**
     * Loc mon an theo danh muc. Truyen null hoac "Tat ca" de lay het.
     * @param danhMuc
     * @return 
     */
    public List<MonAn> getByDanhMuc(String danhMuc) {
        if (danhMuc == null || danhMuc.isBlank() || danhMuc.equalsIgnoreCase("Tat ca")) {
            return getAll();
        }
        return getAll().stream()
                .filter(m -> danhMuc.equals(m.getDanhMuc()))
                .collect(Collectors.toList());
    }

    /**
     * Tim kiem mon theo ten (khong phan biet hoa thuong).
     * @param keyword
     * @return 
     */
    public List<MonAn> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String kw = keyword.toLowerCase();
        return getAll().stream()
                .filter(m -> m.getTenMon().toLowerCase().contains(kw)
                        || m.getMaMon().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    /**
     * Lay danh sach cac danh muc dang co (de do vao combobox loc).
     * @return 
     */
    public List<String> getDanhSachDanhMuc() {
        return getAll().stream()
                .map(MonAn::getDanhMuc)
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Them mon moi. Tu sinh ma neu chua co.
     * @param tenMon
     * @param danhMuc
     * @param donGia
     * @return 
     */
    public MonAn them(String tenMon, String danhMuc, double donGia) {
        validate(tenMon, donGia);
        List<String> ids = getAll().stream().map(MonAn::getMaMon).collect(Collectors.toList());
        String maMoi = IDGenerator.generate("MON", ids, 3);
        MonAn monAn = new MonAn(maMoi, tenMon.trim(), danhMuc, donGia);
        monAnDAO.add(monAn);
        return monAn;
    }

    /**
     * Cap nhat thong tin mon an.
     * @param maMon
     * @param tenMon
     * @param danhMuc
     * @param donGia
     */
    public void capNhat(String maMon, String tenMon, String danhMuc, double donGia) {
        validate(tenMon, donGia);
        MonAn monAn = monAnDAO.findById(maMon)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn: " + maMon));
        monAn.setTenMon(tenMon.trim());
        monAn.setDanhMuc(danhMuc);
        monAn.setDonGia(donGia);
        monAnDAO.update(monAn);
    }

    public void xoa(String maMon) {
        monAnDAO.delete(maMon);
    }

    private void validate(String tenMon, double donGia) {
        if (tenMon == null || tenMon.isBlank()) {
            throw new IllegalArgumentException("Tên món không được để trống.");
        }
        if (donGia <= 0) {
            throw new IllegalArgumentException("Đơn giá phải là số dương (> 0).");
        }
    }
}
