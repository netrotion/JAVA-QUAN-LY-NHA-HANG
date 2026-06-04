package com.restaurant.ui.panel;

import com.restaurant.model.Ban;
import com.restaurant.model.enums.TrangThaiBan;
import com.restaurant.service.BanService;
import com.restaurant.ui.UITheme;
import com.restaurant.util.SessionManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.function.Consumer;

/**
 * So do ban: hien thi luoi cac ban duoi dang the mau.
 * Xanh = trong, Do = dang phuc vu.
 *  - Quan ly: click vao ban de QUAN LY (mo order / sua ten / doi trang thai / xoa); co them nut Them ban + tim kiem.
 *  - Nhan vien phuc vu / Thu ngan: click vao ban de mo order / thanh toan (qua callback onMoOrder).
 */
public class BanPanel extends JPanel {

    private final BanService banService = new BanService();
    private final JPanel grid = new JPanel(new GridLayout(0, 4, 16, 16));
    private final JLabel lblThongKe = new JLabel();
    private final JTextField txtTimKiem = new JTextField(14);

    private Consumer<String> onMoOrder;

    public BanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);

        JPanel gridWrap = new JPanel(new BorderLayout());
        gridWrap.add(grid, BorderLayout.NORTH);
        add(new JScrollPane(gridWrap), BorderLayout.CENTER);

        add(buildLegend(), BorderLayout.SOUTH);

        lamMoi();
    }

    public void setOnMoOrder(Consumer<String> onMoOrder) {
        this.onMoOrder = onMoOrder;
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("SƠ ĐỒ BÀN ĂN");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        lblThongKe.setFont(UITheme.FONT_HEADER);
        right.add(lblThongKe);

        // Tim kiem ban (UC7) - cho moi vai tro
        right.add(new JLabel("  Tìm:"));
        txtTimKiem.addActionListener(e -> apDungLoc());
        right.add(txtTimKiem);
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> apDungLoc());
        right.add(btnTim);
        JButton btnTatCa = new JButton("Tất cả");
        btnTatCa.addActionListener(e -> { txtTimKiem.setText(""); lamMoi(); });
        right.add(btnTatCa);

        // Chi Quan ly moi them ban
        if (SessionManager.isAdmin()) {
            JButton btnThem = new JButton("Thêm bàn");
            btnThem.setBackground(UITheme.PRIMARY);
            btnThem.setForeground(Color.WHITE);
            btnThem.setFocusPainted(false);
            btnThem.addActionListener(e -> themBan());
            right.add(btnThem);
        }
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildLegend() {
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 4));
        legend.add(legendItem(UITheme.BAN_TRONG, "Bàn trống"));
        legend.add(legendItem(UITheme.BAN_PHUC_VU, "Đang phục vụ"));
        String hint = SessionManager.isAdmin()
                ? "Click vào bàn để quản lý (Mở order / sửa / đổi trạng thái / xóa)."
                : "Click vào bàn để mở.";
        JLabel lblHint = new JLabel("   " + hint);
        lblHint.setForeground(Color.GRAY);
        legend.add(lblHint);
        return legend;
    }

    private JPanel legendItem(Color color, String text) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JPanel swatch = new JPanel();
        swatch.setBackground(color);
        swatch.setPreferredSize(new Dimension(20, 20));
        item.add(swatch);
        item.add(new JLabel(text));
        return item;
    }

    public void lamMoi() {
        String kw = txtTimKiem.getText().trim();
        hienThi(kw.isEmpty() ? banService.getAll() : banService.search(kw));
    }

    private void apDungLoc() {
        hienThi(banService.search(txtTimKiem.getText().trim()));
    }

    private void hienThi(List<Ban> dsBan) {
        grid.removeAll();
        int trong = 0;
        int phucVu = 0;
        for (Ban ban : dsBan) {
            grid.add(taoTheBan(ban));
            if (ban.getTrangThai() == TrangThaiBan.TRONG) {
                trong++;
            } else {
                phucVu++;
            }
        }
        lblThongKe.setText("Tong: " + dsBan.size() + "  |  Trong: " + trong + "  |  Phuc vu: " + phucVu);
        grid.revalidate();
        grid.repaint();
    }

    private JButton taoTheBan(Ban ban) {
        boolean trong = ban.getTrangThai() == TrangThaiBan.TRONG;
        JButton card = new JButton("<html><div style='text-align:center;'>"
                + "<b>" + ban.getTenBan() + "</b><br>"
                + ban.getTrangThai().getMoTa() + "</div></html>");
        card.setPreferredSize(new Dimension(180, 110));
        card.setBackground(trong ? UITheme.BAN_TRONG : UITheme.BAN_PHUC_VU);
        card.setForeground(Color.WHITE);
        card.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        card.setFocusPainted(false);
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addActionListener(e -> {
            if (SessionManager.isAdmin()) {
                quanLyBan(ban);
            } else if (onMoOrder != null) {
                onMoOrder.accept(ban.getMaBan());
            }
        });
        return card;
    }

    private void themBan() {
        String ten = JOptionPane.showInputDialog(this, "Tên bàn mới:",
                "Thêm bàn", JOptionPane.PLAIN_MESSAGE);
        if (ten != null && !ten.isBlank()) {
            try {
                banService.them(ten);
                lamMoi();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * UC2 - Quan ly mot ban cu the: mo order, doi ten, doi trang thai, hoac xoa.
     * "Mo order" chi hien khi co callback (Quan ly cung co quyen tao/cap nhat order).
     */
    private void quanLyBan(Ban ban) {
        boolean coOrder = onMoOrder != null;
        String[] luaChon = coOrder
                ? new String[]{"Mở order", "Sửa tên", "Đổi trạng thái", "Xóa bàn", "Đóng"}
                : new String[]{"Sửa tên", "Đổi trạng thái", "Xóa bàn", "Đóng"};
        int chon = JOptionPane.showOptionDialog(this,
                "Bàn: " + ban.getTenBan() + "  (" + ban.getTrangThai().getMoTa() + ")\n"
                        + "Chọn thao tác:",
                "Quản lý bàn " + ban.getMaBan(),
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, luaChon, luaChon[0]);
        if (chon < 0) {
            return; // dong hop thoai
        }
        // Khi co them muc "Mo order" o dau danh sach, cac muc con lai bi day xuong 1 vi tri.
        int offset = coOrder ? 1 : 0;
        if (coOrder && chon == 0) {
            onMoOrder.accept(ban.getMaBan());
            return;
        }

        try {
            switch (chon - offset) {
                case 0 -> suaTenBan(ban);
                case 1 -> doiTrangThaiBan(ban);
                case 2 -> xoaBan(ban);
                default -> { /* dong */ }
            }
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaTenBan(Ban ban) {
        String tenMoi = JOptionPane.showInputDialog(this, "Tên mới cho bàn:",
                ban.getTenBan());
        if (tenMoi != null && !tenMoi.isBlank()) {
            banService.capNhat(ban.getMaBan(), tenMoi);
            lamMoi();
        }
    }

    private void doiTrangThaiBan(Ban ban) {
        boolean trong = ban.getTrangThai() == TrangThaiBan.TRONG;
        String thongDiep = trong
                ? "Đánh dấu bàn này là ĐANG PHỤC VỤ?"
                : "Trả bàn này về TRỐNG?\n(Lưu ý: chỉ nên dùng khi cần điều chỉnh thủ công.)";
        int confirm = JOptionPane.showConfirmDialog(this, thongDiep,
                "Đổi trạng thái", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            banService.doiTrangThai(ban.getMaBan(),
                    trong ? TrangThaiBan.DANG_PHUC_VU : TrangThaiBan.TRONG);
            lamMoi();
        }
    }

    private void xoaBan(Ban ban) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa \"" + ban.getTenBan() + "\"?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            banService.xoa(ban.getMaBan()); // service chan xoa ban dang phuc vu
            lamMoi();
        }
    }
}
