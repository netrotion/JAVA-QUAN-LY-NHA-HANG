package com.restaurant.dao;

import com.restaurant.model.Ban;

import java.util.List;
import java.util.Optional;

/**
 * Truy xuat du lieu ban an.
 */
public class BanDAO {

    private final DataStorage<Ban> storage = new DataStorage<>("ban.dat");

    public List<Ban> findAll() {
        return storage.readAll();
    }

    public Optional<Ban> findById(String maBan) {
        return findAll().stream()
                .filter(b -> b.getMaBan().equals(maBan))
                .findFirst();
    }

    public void add(Ban ban) {
        List<Ban> list = findAll();
        list.add(ban);
        storage.writeAll(list);
    }

    public void update(Ban ban) {
        List<Ban> list = findAll();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaBan().equals(ban.getMaBan())) {
                list.set(i, ban);
                break;
            }
        }
        storage.writeAll(list);
    }

    public void delete(String maBan) {
        List<Ban> list = findAll();
        list.removeIf(b -> b.getMaBan().equals(maBan));
        storage.writeAll(list);
    }

    public void saveAll(List<Ban> list) {
        storage.writeAll(list);
    }

    public boolean isEmpty() {
        return !storage.exists() || findAll().isEmpty();
    }
}
