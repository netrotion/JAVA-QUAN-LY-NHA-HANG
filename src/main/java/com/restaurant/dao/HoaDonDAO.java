package com.restaurant.dao;

import com.restaurant.model.HoaDon;

import java.util.List;
import java.util.Optional;

/**
 * Truy xuat du lieu hoa don.
 */
public class HoaDonDAO {

    private final DataStorage<HoaDon> storage = new DataStorage<>("hoadon.dat");

    public List<HoaDon> findAll() {
        return storage.readAll();
    }

    public Optional<HoaDon> findById(String maHD) {
        return findAll().stream()
                .filter(hd -> hd.getMaHD().equals(maHD))
                .findFirst();
    }

    public void add(HoaDon hoaDon) {
        List<HoaDon> list = findAll();
        list.add(hoaDon);
        storage.writeAll(list);
    }

    public void update(HoaDon hoaDon) {
        List<HoaDon> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaHD().equals(hoaDon.getMaHD())) {
                list.set(i, hoaDon);
                break;
            }
        }
        storage.writeAll(list);
    }

    public void delete(String maHD) {
        List<HoaDon> list = findAll();
        list.removeIf(hd -> hd.getMaHD().equals(maHD));
        storage.writeAll(list);
    }

    public void saveAll(List<HoaDon> list) {
        storage.writeAll(list);
    }
}
