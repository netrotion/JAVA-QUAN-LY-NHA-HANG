package com.restaurant.ui;

import com.restaurant.model.TaiKhoan;
import com.restaurant.ui.panel.BanPanel;
import com.restaurant.ui.panel.HoaDonPanel;
import com.restaurant.ui.panel.NhanVienPanel;
import com.restaurant.ui.panel.OrderPanel;
import com.restaurant.ui.panel.TaiKhoanPanel;
import com.restaurant.ui.panel.ThanhToanPanel;
import com.restaurant.ui.panel.ThongKePanel;
import com.restaurant.ui.panel.ThucDonPanel;
import com.restaurant.util.SessionManager;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Cua so chinh: sidebar dieu huong ben trai, noi dung CardLayout ben phai.
 * Menu hien thi theo dung 3 tac nhan:
 *  - Quan ly (ADMIN)     : So do ban (quan ly), Tao/Cap nhat order, Thuc don, Nhan vien, Tai khoan, Tra cuu hoa don, Bao cao.
 *  - Nhan vien phuc vu   : So do ban, Tao/Cap nhat order.
 *  - Thu ngan (THU_NGAN) : So do ban, Thanh toan, Tra cuu/In hoa don.
 */
public class MainFrame extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contentPanel = new JPanel(cardLayout);
    private final List<JButton> navButtons = new ArrayList<>();
    private static final String DEFAULT_CARD = "BAN";

    private BanPanel banPanel;
    private OrderPanel orderPanel;
    private ThanhToanPanel thanhToanPanel;

    public MainFrame() {
        TaiKhoan user = SessionManager.getCurrentUser();
        setTitle("Quản lý nhà hàng - " + (user != null ? user.getTenHienThi() : ""));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1180, 720);
        setMinimumSize(new Dimension(1024, 640));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(buildContent(), BorderLayout.CENTER);
        add(buildSidebar(), BorderLayout.WEST);
    }

    private JPanel buildContent() {
        boolean isAdmin = SessionManager.isAdmin();
        boolean isNhanVien = SessionManager.isNhanVien();
        boolean isThuNgan = SessionManager.isThuNgan();

        banPanel = new BanPanel();
        contentPanel.add(banPanel, "BAN");

        if (isNhanVien || isAdmin) {
            // Nhan vien phuc vu va Quan ly: tao/cap nhat order
            orderPanel = new OrderPanel();
            orderPanel.setOnThayDoi(() -> banPanel.lamMoi());
            // Voi Quan ly, click vao the ban mac dinh mo hop thoai quan ly ban;
            // chon "Mo order" trong hop thoai do se goi callback nay.
            banPanel.setOnMoOrder(maBan -> {
                orderPanel.moBan(maBan);
                showCard("ORDER");
            });
            contentPanel.add(orderPanel, "ORDER");
        }

        if (isThuNgan) {
            // Thu ngan: thanh toan + tra cuu/in hoa don
            thanhToanPanel = new ThanhToanPanel();
            thanhToanPanel.setOnThayDoi(() -> banPanel.lamMoi());
            banPanel.setOnMoOrder(maBan -> {
                thanhToanPanel.moBan(maBan);
                showCard("THANHTOAN");
            });
            contentPanel.add(thanhToanPanel, "THANHTOAN");
            contentPanel.add(new HoaDonPanel(), "HOADON");
        }

        if (isAdmin) {
            // Quan ly: du lieu nen + tra cuu + bao cao (khong order/thanh toan)
            contentPanel.add(new ThucDonPanel(), "THUCDON");
            contentPanel.add(new NhanVienPanel(), "NHANVIEN");
            contentPanel.add(new TaiKhoanPanel(), "TAIKHOAN");
            contentPanel.add(new HoaDonPanel(), "HOADON");
            contentPanel.add(new ThongKePanel(), "THONGKE");
        }

        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        return contentPanel;
    }

    private JPanel buildSidebar() {
        TaiKhoan user = SessionManager.getCurrentUser();
        boolean isAdmin = SessionManager.isAdmin();
        boolean isNhanVien = SessionManager.isNhanVien();
        boolean isThuNgan = SessionManager.isThuNgan();

        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UITheme.SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel logo = new JLabel("NHÀ HÀNG", SwingConstants.CENTER);
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        logo.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        sidebar.add(logo);

        JLabel role = new JLabel(user != null ? user.getVaiTro().getMoTa() : "", SwingConstants.CENTER);
        role.setForeground(UITheme.ACCENT);
        role.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        role.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(role);

        sidebar.add(Box.createVerticalStrut(24));

        // Tat ca vai tro deu thay so do ban
        sidebar.add(navButton("Sơ đồ bàn", "BAN"));

        if (isNhanVien || isAdmin) {
            sidebar.add(navButton("Tạo / Cập nhật order", "ORDER"));
        }
        if (isThuNgan) {
            sidebar.add(navButton("Thanh toán", "THANHTOAN"));
            sidebar.add(navButton("Tra cứu / In hóa đơn", "HOADON"));
        }
        if (isAdmin) {
            sidebar.add(navButton("Thực đơn", "THUCDON"));
            sidebar.add(navButton("Nhân viên", "NHANVIEN"));
            sidebar.add(navButton("Tài khoản", "TAIKHOAN"));
            sidebar.add(navButton("Tra cứu hóa đơn", "HOADON"));
            sidebar.add(navButton("Báo cáo", "THONGKE"));
        }

        sidebar.add(Box.createVerticalGlue());

        JButton btnLogout = navButton("Đăng xuất", null);
        btnLogout.addActionListener(e -> dangXuat());
        sidebar.add(btnLogout);

        return sidebar;
    }

    private JButton navButton(String text, String card) {
        JButton btn = new JButton(text);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 10));
        btn.setBackground(UITheme.SIDEBAR_BG);
        btn.setForeground(UITheme.SIDEBAR_FG);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);

        if (card != null) {
            navButtons.add(btn);
            btn.addActionListener(e -> {
                showCard(card);
                highlight(btn);
            });
        }
        return btn;
    }

    private void highlight(JButton active) {
        for (JButton b : navButtons) {
            b.setBackground(UITheme.SIDEBAR_BG);
            b.setForeground(UITheme.SIDEBAR_FG);
        }
        active.setBackground(UITheme.PRIMARY);
        active.setForeground(Color.WHITE);
    }

    private void showCard(String card) {
        if ("ORDER".equals(card) && orderPanel != null) {
            orderPanel.lamMoi();
        }
        if ("THANHTOAN".equals(card) && thanhToanPanel != null) {
            thanhToanPanel.lamMoi();
        }
        if ("BAN".equals(card) && banPanel != null) {
            banPanel.lamMoi();
        }
        cardLayout.show(contentPanel, card);
    }

    private void dangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b && !navButtons.isEmpty()) {
            showCard(DEFAULT_CARD);
            highlight(navButtons.get(0));
        }
    }
}
