package com.restaurant.dao;

import com.restaurant.model.NhanVien;

import java.util.List;
import java.util.Optional;

/**
 * Truy xuat du lieu nhan vien.
 */
public class NhanVienDAO {

    private final DataStorage<NhanVien> storage = new DataStorage<>("nhanvien.dat");

    public List<NhanVien> findAll() {
        return storage.readAll();
    }

    public Optional<NhanVien> findById(String maNV) {
        return findAll().stream()
                .filter(nv -> nv.getMaNV().equals(maNV))
                .findFirst();
    }

    public void add(NhanVien nhanVien) {
        List<NhanVien> list = findAll();
        list.add(nhanVien);
        storage.writeAll(list);
    }

    public void update(NhanVien nhanVien) {
        List<NhanVien> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaNV().equals(nhanVien.getMaNV())) {
                list.set(i, nhanVien);
                break;
            }
        }
        storage.writeAll(list);
    }

    public void delete(String maNV) {
        List<NhanVien> list = findAll();
        list.removeIf(nv -> nv.getMaNV().equals(maNV));
        storage.writeAll(list);
    }

    public void saveAll(List<NhanVien> list) {
        storage.writeAll(list);
    }

    public boolean isEmpty() {
        return !storage.exists() || findAll().isEmpty();
    }
}
