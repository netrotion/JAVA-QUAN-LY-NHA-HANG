package com.restaurant.service;

import com.restaurant.dao.HoaDonDAO;
import com.restaurant.model.HoaDon;
import com.restaurant.model.MonAn;
import com.restaurant.model.TaiKhoan;
import com.restaurant.model.enums.TrangThaiHoaDon;
import com.restaurant.util.IDGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Nghiep vu order va thanh toan.
 * Phoi hop voi BanService de cap nhat trang thai ban.
 */
public class HoaDonService {

    private final HoaDonDAO hoaDonDAO = new HoaDonDAO();
    private final BanService banService = new BanService();

    public List<HoaDon> getAll() {
        return hoaDonDAO.findAll();
    }

    public List<HoaDon> getDaThanhToan() {
        return getAll().stream()
                .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.DA_THANH_TOAN)
                .collect(Collectors.toList());
    }

    /**
     * Lay hoa don dang mo (chua thanh toan) cua mot ban, neu co.
     */
    public Optional<HoaDon> getHoaDonDangMo(String maBan) {
        return getAll().stream()
                .filter(hd -> hd.getMaBan().equals(maBan)
                        && hd.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN)
                .findFirst();
    }

    /**
     * Danh sach tat ca hoa don dang mo (chua thanh toan) - dung cho man hinh Thu ngan.
     */
    public List<HoaDon> getDanhSachDangMo() {
        return getAll().stream()
                .filter(hd -> hd.getTrangThai() == TrangThaiHoaDon.CHUA_THANH_TOAN)
                .collect(Collectors.toList());
    }

    /**
     * Mo hoa don cho ban: neu da co hoa don dang mo thi tra ve, neu chua thi tao moi.
     * Khi tao moi, ban se chuyen sang trang thai dang phuc vu.
     */
    public HoaDon moHoaDon(String maBan, String tenBan, TaiKhoan nhanVien) {
        Optional<HoaDon> dangMo = getHoaDonDangMo(maBan);
        if (dangMo.isPresent()) {
            return dangMo.get();
        }
        List<String> ids = getAll().stream().map(HoaDon::getMaHD).collect(Collectors.toList());
        String maMoi = IDGenerator.generate("HD", ids, 5);
        String maNV = nhanVien != null ? nhanVien.getMaNV() : "";
        String tenNV = nhanVien != null ? nhanVien.getTenHienThi() : "";
        HoaDon hoaDon = new HoaDon(maMoi, maBan, tenBan, maNV, tenNV);
        hoaDonDAO.add(hoaDon);
        banService.datBan(maBan);
        return hoaDon;
    }

    public void themMon(HoaDon hoaDon, MonAn monAn, int soLuong) {
        if (soLuong <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }
        hoaDon.themMon(monAn, soLuong);
        hoaDonDAO.update(hoaDon);
    }

    public void xoaMon(HoaDon hoaDon, String maMon) {
        hoaDon.xoaMon(maMon);
        hoaDonDAO.update(hoaDon);
    }

    public void capNhatSoLuong(HoaDon hoaDon, String maMon, int soLuong) {
        if (soLuong <= 0) {
            hoaDon.xoaMon(maMon);
        } else {
            hoaDon.getDanhSachChiTiet().stream()
                    .filter(ct -> ct.getMaMon().equals(maMon))
                    .findFirst()
                    .ifPresent(ct -> ct.setSoLuong(soLuong));
        }
        hoaDonDAO.update(hoaDon);
    }

    /**
     * Thanh toan hoa don: danh dau da thanh toan, ghi thoi gian, tra ban ve trong.
     *
     * @param hoaDon hoa don can thanh toan
     * @param chietKhau % chiet khau (0-100)
     * @param tienKhachDua so tien khach dua
     */
    public void thanhToan(HoaDon hoaDon, double chietKhau, double tienKhachDua) {
        if (hoaDon.getDanhSachChiTiet().isEmpty()) {
            throw new IllegalStateException("Hóa đơn chưa có món nào, không thể thanh toán.");
        }
        if (chietKhau < 0 || chietKhau > 100) {
            throw new IllegalArgumentException("Chiết khẩu phải nằm trong khoảng 0-100%.");
        }
        hoaDon.setChietKhau(chietKhau);
        double tongThanhToan = hoaDon.getTongThanhToan();

        if (tienKhachDua < tongThanhToan) {
            throw new IllegalArgumentException(
                String.format("Tiền khách đưa (%.0f) không đủ. Cần: %.0f", tienKhachDua, tongThanhToan));
        }

        hoaDon.setTienKhachDua(tienKhachDua);
        hoaDon.setTienThua(tienKhachDua - tongThanhToan);
        hoaDon.setTrangThai(TrangThaiHoaDon.DA_THANH_TOAN);
        hoaDon.setThoiGianThanhToan(LocalDateTime.now());
        hoaDonDAO.update(hoaDon);
        banService.traBan(hoaDon.getMaBan());
    }

    /**
     * Huy hoa don dang mo va tra ban ve trong.
     */
    public void huyHoaDon(HoaDon hoaDon) {
        hoaDon.setTrangThai(TrangThaiHoaDon.DA_HUY);
        hoaDonDAO.update(hoaDon);
        banService.traBan(hoaDon.getMaBan());
    }

    public double tongDoanhThu() {
        return getDaThanhToan().stream()
                .mapToDouble(HoaDon::getTongTien)
                .sum();
    }

    /**
     * Loc hoa don da thanh toan theo khoang thoi gian.
     *
     * @param tuNgay thoi gian bat dau (null = khong gioi han)
     * @param denNgay thoi gian ket thuc (null = khong gioi han)
     */
    public List<HoaDon> locTheoThoiGian(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return getDaThanhToan().stream()
                .filter(hd -> {
                    LocalDateTime thoiGian = hd.getThoiGianThanhToan();
                    if (thoiGian == null) return false;
                    if (tuNgay != null && thoiGian.isBefore(tuNgay)) return false;
                    if (denNgay != null && thoiGian.isAfter(denNgay)) return false;
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Tinh tong doanh thu trong khoang thoi gian.
     */
    public double tongDoanhThuTheoKy(LocalDateTime tuNgay, LocalDateTime denNgay) {
        return locTheoThoiGian(tuNgay, denNgay).stream()
                .mapToDouble(HoaDon::getTongThanhToan)
                .sum();
    }

    /**
     * Tinh top mon ban chay nhat trong khoang thoi gian (dung HashMap).
     * Tra ve danh sach [Ten mon, So luong ban] sap xep giam dan theo so luong.
     */
    public List<java.util.Map.Entry<String, Integer>> topMonBanChay(LocalDateTime tuNgay, LocalDateTime denNgay, int top) {
        List<HoaDon> dsHD = locTheoThoiGian(tuNgay, denNgay);
        java.util.Map<String, Integer> thongKe = new java.util.HashMap<>();

        for (HoaDon hd : dsHD) {
            for (com.restaurant.model.ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                thongKe.merge(ct.getTenMon(), ct.getSoLuong(), Integer::sum);
            }
        }

        return thongKe.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()))
                .limit(top)
                .collect(Collectors.toList());
    }
}
