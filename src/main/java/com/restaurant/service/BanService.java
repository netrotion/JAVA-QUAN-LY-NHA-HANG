package com.restaurant.service;

import com.restaurant.dao.BanDAO;
import com.restaurant.model.Ban;
import com.restaurant.model.enums.TrangThaiBan;
import com.restaurant.util.IDGenerator;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Nghiep vu quan ly ban an.
 */
public class BanService {

    private final BanDAO banDAO = new BanDAO();

    public List<Ban> getAll() {
        return banDAO.findAll();
    }

    /**
     * UC7 - tim ban theo tu khoa (ma ban hoac ten ban).
     */
    public List<Ban> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAll();
        }
        String kw = keyword.trim().toLowerCase();
        return getAll().stream()
                .filter(b -> b.getMaBan().toLowerCase().contains(kw)
                        || b.getTenBan().toLowerCase().contains(kw))
                .collect(Collectors.toList());
    }

    public Ban getById(String maBan) {
        return banDAO.findById(maBan)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bàn: " + maBan));
    }

    public java.util.Optional<Ban> findByIdSafe(String maBan) {
        return banDAO.findById(maBan);
    }

    public Ban them(String tenBan) {
        if (tenBan == null || tenBan.isBlank()) {
            throw new IllegalArgumentException("Tên bàn không được để trống.");
        }
        kiemTraTrungTen(tenBan, null);
        List<String> ids = getAll().stream().map(Ban::getMaBan).collect(Collectors.toList());
        String maMoi = IDGenerator.generate("BAN", ids, 3);
        Ban ban = new Ban(maMoi, tenBan.trim(), TrangThaiBan.TRONG);
        banDAO.add(ban);
        return ban;
    }

    public void capNhat(String maBan, String tenBan) {
        if (tenBan == null || tenBan.isBlank()) {
            throw new IllegalArgumentException("Tên bàn không được để trống.");
        }
        kiemTraTrungTen(tenBan, maBan);
        Ban ban = getById(maBan);
        ban.setTenBan(tenBan.trim());
        banDAO.update(ban);
    }

    /**
     * Chan trung ten ban (khong phan biet hoa thuong, da bo khoang trang dau/cuoi).
     *
     * @param tenBan    ten can kiem tra
     * @param maBanBoQua ma ban duoc bo qua khi so sanh (dung khi cap nhat chinh no); null khi them moi
     */
    private void kiemTraTrungTen(String tenBan, String maBanBoQua) {
        String ten = tenBan.trim();
        boolean trung = getAll().stream()
                .filter(b -> maBanBoQua == null || !b.getMaBan().equals(maBanBoQua))
                .anyMatch(b -> b.getTenBan().equalsIgnoreCase(ten));
        if (trung) {
            throw new IllegalArgumentException("Ten ban \"" + ten + "\" da ton tai.");
        }
    }

    public void xoa(String maBan) {
        Ban ban = getById(maBan);
        if (ban.getTrangThai() == TrangThaiBan.DANG_PHUC_VU) {
            throw new IllegalStateException("Khong the xoa ban dang phuc vu.");
        }
        banDAO.delete(maBan);
    }

    public void doiTrangThai(String maBan, TrangThaiBan trangThai) {
        Ban ban = getById(maBan);
        ban.setTrangThai(trangThai);
        banDAO.update(ban);
    }

    public void datBan(String maBan) {
        doiTrangThai(maBan, TrangThaiBan.DANG_PHUC_VU);
    }

    public void traBan(String maBan) {
        doiTrangThai(maBan, TrangThaiBan.TRONG);
    }
}
