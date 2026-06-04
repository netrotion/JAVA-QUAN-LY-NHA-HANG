package com.restaurant.dao;

import com.restaurant.model.MonAn;

import java.util.List;
import java.util.Optional;

/**
 * Truy xuat du lieu mon an.
 */
public class MonAnDAO {

    private final DataStorage<MonAn> storage = new DataStorage<>("monan.dat");

    public List<MonAn> findAll() {
        return storage.readAll();
    }

    public Optional<MonAn> findById(String maMon) {
        return findAll().stream()
                .filter(m -> m.getMaMon().equals(maMon))
                .findFirst();
    }

    public void add(MonAn monAn) {
        List<MonAn> list = findAll();
        list.add(monAn);
        storage.writeAll(list);
    }

    public void update(MonAn monAn) {
        List<MonAn> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaMon().equals(monAn.getMaMon())) {
                list.set(i, monAn);
                break;
            }
        }
        storage.writeAll(list);
    }

    public void delete(String maMon) {
        List<MonAn> list = findAll();
        list.removeIf(m -> m.getMaMon().equals(maMon));
        storage.writeAll(list);
    }

    public void saveAll(List<MonAn> list) {
        storage.writeAll(list);
    }

    public boolean isEmpty() {
        return !storage.exists() || findAll().isEmpty();
    }
}
