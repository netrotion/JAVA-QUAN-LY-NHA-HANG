package com.restaurant.util;

import com.restaurant.model.TaiKhoan;

/**
 * Giu thong tin tai khoan dang dang nhap trong phien lam viec.
 */
public final class SessionManager {

    private static TaiKhoan currentUser;

    private SessionManager() {
    }

    public static void setCurrentUser(TaiKhoan user) {
        currentUser = user;
    }

    public static TaiKhoan getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return currentUser != null && currentUser.isAdmin();
    }

    /** Nhan vien phuc vu: tao/cap nhat order (Quan ly cung co quyen nay).
     * @return  */
    public static boolean isNhanVien() {
        return currentUser != null && currentUser.isNhanVien();
    }

    /** Thu ngan: chi thanh toan hoa don.
     * @return  */
    public static boolean isThuNgan() {
        return currentUser != null && currentUser.isThuNgan();
    }

    public static void logout() {
        currentUser = null;
    }
}
