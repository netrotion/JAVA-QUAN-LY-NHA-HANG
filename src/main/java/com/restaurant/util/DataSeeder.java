package com.restaurant.util;

import com.restaurant.dao.BanDAO;
import com.restaurant.dao.MonAnDAO;
import com.restaurant.dao.NhanVienDAO;
import com.restaurant.dao.TaiKhoanDAO;
import com.restaurant.model.Ban;
import com.restaurant.model.MonAn;
import com.restaurant.model.NhanVien;
import com.restaurant.model.TaiKhoan;
import com.restaurant.model.enums.CaLamViec;
import com.restaurant.model.enums.TrangThaiBan;
import com.restaurant.model.enums.VaiTro;

import java.util.ArrayList;
import java.util.List;

/**
 * Tao du lieu mau (example data) khi chay lan dau (cac file du lieu con trong).
 */
public final class DataSeeder {

    private DataSeeder() {
    }

    public static void seedIfEmpty() {
        seedMonAn();
        seedBan();
        seedNhanVien();
        seedTaiKhoan();
        // Dam bao luon co tai khoan Thu ngan (ke ca khi du lieu cu da ton tai).
        ensureThuNgan();
    }

    private static void seedMonAn() {
        MonAnDAO dao = new MonAnDAO();
        if (!dao.isEmpty()) {
            return;
        }
        List<MonAn> list = new ArrayList<>();
        list.add(new MonAn("MON001", "Gỏi cuốn tôm thịt", "Khai vị", 45000));
        list.add(new MonAn("MON002", "Súp cua", "Khai vị", 55000));
        list.add(new MonAn("MON003", "Cơm gà xối mỡ", "Món chính", 65000));
        list.add(new MonAn("MON004", "Bò lúc lắc", "Món chính", 120000));
        list.add(new MonAn("MON005", "Lẩu thái hải sản", "Món chính", 250000));
        list.add(new MonAn("MON006", "Ca kho to", "Món chính", 90000));
        list.add(new MonAn("MON007", "Trà đá", "Đồ uống", 5000));
        list.add(new MonAn("MON008", "Nước cam ép", "Đồ uống", 30000));
        list.add(new MonAn("MON009", "Bia Sài Gòn", "Đồ uống", 20000));
        list.add(new MonAn("MON010", "Kem Tráng Miệng", "Tráng miệng", 25000));
        dao.saveAll(list);
    }

    private static void seedBan() {
        BanDAO dao = new BanDAO();
        if (!dao.isEmpty()) {
            return;
        }
        List<Ban> list = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            String ma = String.format("BAN%03d", i);
            list.add(new Ban(ma, "Bàn " + i, TrangThaiBan.TRONG));
        }
        dao.saveAll(list);
    }

    private static void seedNhanVien() {
        NhanVienDAO dao = new NhanVienDAO();
        if (!dao.isEmpty()) {
            return;
        }
        List<NhanVien> list = new ArrayList<>();
        list.add(new NhanVien("NV001", "Nguyễn Văn An", CaLamViec.SANG, "0901234567", 150000));
        list.add(new NhanVien("NV002", "Trần Thị Bình", CaLamViec.CHIEU, "0912345678", 150000));
        list.add(new NhanVien("NV003", "Lê Văn Cương", CaLamViec.TOI, "0923456789", 180000));
        list.add(new NhanVien("NV004", "Phạm Thu Ngân", CaLamViec.SANG, "0934567890", 160000));
        dao.saveAll(list);
    }

    private static void seedTaiKhoan() {
        TaiKhoanDAO dao = new TaiKhoanDAO();
        if (!dao.isEmpty()) {
            return;
        }
        List<TaiKhoan> list = new ArrayList<>();
        list.add(new TaiKhoan("admin", "admin", VaiTro.ADMIN, "", "Quản trị viên"));
        list.add(new TaiKhoan("nv01", "123", VaiTro.NHAN_VIEN, "NV001", "Nguyen Van An"));
        list.add(new TaiKhoan("tn01", "123", VaiTro.THU_NGAN, "NV004", "Phạm Thu Ngân"));
        dao.saveAll(list);
    }

    /**
     * Bo sung tai khoan Thu ngan mac dinh neu chua co (du lieu cu chi co admin/nv01).
     * Khong ghi de neu da ton tai tai khoan thu ngan nao do.
     */
    private static void ensureThuNgan() {
        TaiKhoanDAO dao = new TaiKhoanDAO();
        if (dao.isEmpty()) {
            return; // seedTaiKhoan() da tao day du o lan chay dau.
        }
        List<TaiKhoan> list = dao.findAll();
        boolean coThuNgan = list.stream().anyMatch(tk -> tk.getVaiTro() == VaiTro.THU_NGAN);
        boolean trungUsername = list.stream().anyMatch(tk -> "tn01".equals(tk.getUsername()));
        if (!coThuNgan && !trungUsername) {
            dao.add(new TaiKhoan("tn01", "123", VaiTro.THU_NGAN, "", "Thu ngân"));
        }
    }
}
