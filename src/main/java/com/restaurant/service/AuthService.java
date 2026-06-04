package com.restaurant.service;

import com.restaurant.dao.TaiKhoanDAO;
import com.restaurant.model.TaiKhoan;
import com.restaurant.model.enums.VaiTro;

import java.util.List;
import java.util.Optional;

/**
 * Nghiep vu dang nhap va quan ly tai khoan.
 */
public class AuthService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Kiem tra dang nhap. Tra ve tai khoan neu dung, rong neu sai.
     */
    public Optional<TaiKhoan> dangNhap(String username, String password) {
        if (username == null || password == null) {
            return Optional.empty();
        }
        return taiKhoanDAO.findByUsername(username.trim())
                .filter(tk -> tk.getPassword().equals(password));
    }

    public List<TaiKhoan> getAll() {
        return taiKhoanDAO.findAll();
    }

    public TaiKhoan them(String username, String password, VaiTro vaiTro, String maNV, String tenHienThi) {
        validate(username, password);
        if (taiKhoanDAO.findByUsername(username.trim()).isPresent()) {
            throw new IllegalArgumentException("Tên đăng nhập đã tồn tại.");
        }
        TaiKhoan tk = new TaiKhoan(username.trim(), password, vaiTro, maNV, tenHienThi);
        taiKhoanDAO.add(tk);
        return tk;
    }

    public void capNhat(String username, String password, VaiTro vaiTro, String maNV, String tenHienThi) {
        TaiKhoan tk = taiKhoanDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản: " + username));
        if (password != null && !password.isBlank()) {
            tk.setPassword(password);
        }
        tk.setVaiTro(vaiTro);
        tk.setMaNV(maNV);
        tk.setTenHienThi(tenHienThi);
        taiKhoanDAO.update(tk);
    }

    public void doiMatKhau(String username, String matKhauCu, String matKhauMoi) {
        TaiKhoan tk = taiKhoanDAO.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy tài khoản."));
        if (!tk.getPassword().equals(matKhauCu)) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng.");
        }
        if (matKhauMoi == null || matKhauMoi.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu mới không được để trống.");
        }
        tk.setPassword(matKhauMoi);
        taiKhoanDAO.update(tk);
    }

    public void xoa(String username) {
        taiKhoanDAO.delete(username);
    }

    private void validate(String username, String password) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Tên đăng nhập không được để trống.");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống.");
        }
    }
}
