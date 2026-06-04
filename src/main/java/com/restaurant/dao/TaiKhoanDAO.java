package com.restaurant.dao;

import com.restaurant.model.TaiKhoan;

import java.util.List;
import java.util.Optional;

/**
 * Truy xuat du lieu tai khoan dang nhap.
 */
public class TaiKhoanDAO {

    private final DataStorage<TaiKhoan> storage = new DataStorage<>("taikhoan.dat");

    public List<TaiKhoan> findAll() {
        return storage.readAll();
    }

    public Optional<TaiKhoan> findByUsername(String username) {
        return findAll().stream()
                .filter(tk -> tk.getUsername().equals(username))
                .findFirst();
    }

    public void add(TaiKhoan taiKhoan) {
        List<TaiKhoan> list = findAll();
        list.add(taiKhoan);
        storage.writeAll(list);
    }

    public void update(TaiKhoan taiKhoan) {
        List<TaiKhoan> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUsername().equals(taiKhoan.getUsername())) {
                list.set(i, taiKhoan);
                break;
            }
        }
        storage.writeAll(list);
    }

    public void delete(String username) {
        List<TaiKhoan> list = findAll();
        list.removeIf(tk -> tk.getUsername().equals(username));
        storage.writeAll(list);
    }

    public void saveAll(List<TaiKhoan> list) {
        storage.writeAll(list);
    }

    public boolean isEmpty() {
        return !storage.exists() || findAll().isEmpty();
    }
}
