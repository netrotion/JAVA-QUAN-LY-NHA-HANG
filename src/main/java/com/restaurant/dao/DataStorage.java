package com.restaurant.dao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Luu / doc danh sach doi tuong xuong file .dat bang Java Serialization.
 * Tat ca du lieu duoc luu trong thu muc "data" o thu muc chay chuong trinh.
 *
 * @param <T> kieu doi tuong (phai implements Serializable)
 */
public class DataStorage<T> {

    private static final String DATA_DIR = "data";
    private final File file;

    public DataStorage(String fileName) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.file = new File(dir, fileName);
    }

    /**
     * Doc toan bo danh sach tu file. Tra ve list rong neu file chua ton tai.
     */
    @SuppressWarnings("unchecked")
    public List<T> readAll() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof List) {
                return (List<T>) obj;
            }
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Lỗi đọc file " + file.getName() + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Ghi de toan bo danh sach xuong file.
     */
    public void writeAll(List<T> data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(new ArrayList<>(data));
        } catch (IOException e) {
            System.err.println("Loi ghi file " + file.getName() + ": " + e.getMessage());
        }
    }

    /**
     * Kiem tra file du lieu da ton tai chua.
     */
    public boolean exists() {
        return file.exists();
    }
}
