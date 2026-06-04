package com.restaurant;

import com.formdev.flatlaf.FlatLightLaf;
import com.restaurant.ui.LoginFrame;
import com.restaurant.util.DataSeeder;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Diem khoi dong ung dung Quan Ly Nha Hang.
 */
public class Main {

    public static void main(String[] args) {
        // Thiet lap giao dien hien dai FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Button.arc", 8);
            UIManager.put("Component.arc", 8);
            UIManager.put("TextComponent.arc", 8);
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Không thể load FlatLaf, dùng giao diện mặc định: " + e.getMessage());
        }

        // Tao du lieu mau neu chay lan dau
        DataSeeder.seedIfEmpty();

        // Mo man hinh dang nhap
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null,
                        "Lỗi khởi động ứng dụng: " + ex.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
}
