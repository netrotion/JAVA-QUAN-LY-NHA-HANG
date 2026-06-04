package com.restaurant.ui.panel;

import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;
import com.restaurant.service.HoaDonService;
import com.restaurant.ui.UITheme;
import com.restaurant.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;

/**
 * UC6 Thanh toan hoa don / UC9 Xem-In hoa don - danh cho THU NGAN.
 * Trai: danh sach order dang mo (chua thanh toan).
 * Phai: chi tiet order duoc chon + chiet khau / tien khach dua / tien thua + nut thanh toan.
 * Thanh toan xong se luu hoa don va giai phong ban ve trong.
 */
public class ThanhToanPanel extends JPanel {

    private final HoaDonService hoaDonService = new HoaDonService();

    private final DefaultTableModel dsModel = new DefaultTableModel(
            new Object[]{"Mã HD", "Bàn", "Nhân viên", "Tạm tính"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable dsTable = new JTable(dsModel);

    private final DefaultTableModel ctModel = new DefaultTableModel(
            new Object[]{"Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable ctTable = new JTable(ctModel);

    private final JLabel lblTongTien = new JLabel("0 d");
    private final JTextField txtChietKhau = new JTextField("0", 5);
    private final JTextField txtTienKhachDua = new JTextField(10);
    private final JLabel lblThanhToan = new JLabel("0 d");
    private final JLabel lblTienThua = new JLabel("0 d");

    private HoaDon hoaDonHienTai;
    private Runnable onThayDoi;

    public ThanhToanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeft(), buildRight());
        split.setResizeWeight(0.42);
        split.setDividerLocation(470);
        add(split, BorderLayout.CENTER);

        dsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                onChonHoaDon();
            }
        });

        lamMoi();
    }

    public void setOnThayDoi(Runnable onThayDoi) {
        this.onThayDoi = onThayDoi;
    }

    /**
     * Lam moi danh sach va chon san order dang mo cua ban (goi tu so do ban).
     */
    public void moBan(String maBan) {
        lamMoi();
        for (int i = 0; i < dsModel.getRowCount(); i++) {
            String maHD = dsModel.getValueAt(i, 0).toString();
            boolean khopBan = hoaDonService.getAll().stream()
                    .anyMatch(hd -> hd.getMaHD().equals(maHD) && hd.getMaBan().equals(maBan));
            if (khopBan) {
                dsTable.setRowSelectionInterval(i, i);
                onChonHoaDon();
                return;
            }
        }
        JOptionPane.showMessageDialog(this,
                "Bàn này chưa có order đang chờ thanh toán.",
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("THANH TOÁN (THU NGÂN)");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JButton btnLamMoi = new JButton("Làm mới danh sách");
        btnLamMoi.addActionListener(e -> lamMoi());
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(btnLamMoi);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildLeft() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Order đang chờ thanh toán"));
        dsTable.setRowHeight(28);
        dsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(dsTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRight() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết & thanh toán"));

        ctTable.setRowHeight(26);
        panel.add(new JScrollPane(ctTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 6));

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 6));
        form.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JLabel lblTongLbl = new JLabel("Tổng hàng:");
        lblTongLbl.setFont(UITheme.FONT_HEADER);
        lblTongTien.setFont(UITheme.FONT_HEADER);
        form.add(lblTongLbl);
        form.add(lblTongTien);

        form.add(new JLabel("Chiết khấu (%):"));
        txtChietKhau.addActionListener(e -> capNhatTinhToan());
        txtChietKhau.addFocusListener(new java.awt.event.FocusAdapter() {
//            @Override
            public void focusLost(java.awt.event.FocusEvent e) { capNhatTinhToan(); }
        });
        form.add(txtChietKhau);

        JLabel lblTtLbl = new JLabel("Thanh toán:");
        lblTtLbl.setFont(UITheme.FONT_HEADER);
        lblThanhToan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblThanhToan.setForeground(UITheme.DANGER);
        form.add(lblTtLbl);
        form.add(lblThanhToan);

        form.add(new JLabel("Tiền khách đưa:"));
        txtTienKhachDua.addActionListener(e -> capNhatTinhToan());
        txtTienKhachDua.addFocusListener(new java.awt.event.FocusAdapter() {
//            @Override
            public void focusLost(java.awt.event.FocusEvent e) { capNhatTinhToan(); }
        });
        form.add(txtTienKhachDua);

        south.add(form, BorderLayout.NORTH);

        JPanel thuaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JLabel lblThuaLbl = new JLabel("Tiền thừa: ");
        lblThuaLbl.setFont(UITheme.FONT_HEADER);
        lblTienThua.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTienThua.setForeground(UITheme.PRIMARY);
        thuaPanel.add(lblThuaLbl);
        thuaPanel.add(lblTienThua);
        south.add(thuaPanel, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 8, 0));
        JButton btnXem = new JButton("Xem / In hóa đơn");
        JButton btnThanhToan = new JButton("XÁC NHẬN THANH TOÁN");

        btnXem.addActionListener(e -> xemHoaDon());
        btnThanhToan.addActionListener(e -> thanhToan());

        btnThanhToan.setBackground(UITheme.PRIMARY);
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setFont(UITheme.FONT_BUTTON);
        btnThanhToan.setFocusPainted(false);
        btnXem.setBackground(UITheme.ACCENT);
        btnXem.setForeground(Color.WHITE);
        btnXem.setFocusPainted(false);

        buttons.add(btnXem);
        buttons.add(btnThanhToan);
        south.add(buttons, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ===== Logic =====

    public void lamMoi() {
        hoaDonHienTai = null;
        dsModel.setRowCount(0);
        List<HoaDon> dsMo = hoaDonService.getDanhSachDangMo();
        for (HoaDon hd : dsMo) {
            dsModel.addRow(new Object[]{
                    hd.getMaHD(), hd.getTenBan(), hd.getTenNV(),
                    Formatter.money(hd.getTongTien())
            });
        }
        ctModel.setRowCount(0);
        txtChietKhau.setText("0");
        txtTienKhachDua.setText("");
        lblTongTien.setText("0 d");
        lblThanhToan.setText("0 d");
        lblTienThua.setText("0 d");
    }

    private void onChonHoaDon() {
        int row = dsTable.getSelectedRow();
        if (row < 0) {
            hoaDonHienTai = null;
            ctModel.setRowCount(0);
            return;
        }
        String maHD = dsModel.getValueAt(row, 0).toString();
        hoaDonHienTai = hoaDonService.getAll().stream()
                .filter(hd -> hd.getMaHD().equals(maHD))
                .findFirst().orElse(null);
        ctModel.setRowCount(0);
        if (hoaDonHienTai != null) {
            for (ChiTietHoaDon ct : hoaDonHienTai.getDanhSachChiTiet()) {
                ctModel.addRow(new Object[]{
                        ct.getMaMon(), ct.getTenMon(), ct.getSoLuong(),
                        Formatter.money(ct.getDonGia()), Formatter.money(ct.getThanhTien())
                });
            }
        }
        txtChietKhau.setText("0");
        txtTienKhachDua.setText("");
        capNhatTinhToan();
    }

    private void capNhatTinhToan() {
        if (hoaDonHienTai == null) {
            lblTongTien.setText("0 d");
            lblThanhToan.setText("0 d");
            lblTienThua.setText("0 d");
            return;
        }
        lblTongTien.setText(Formatter.money(hoaDonHienTai.getTongTien()));
        try {
            double chietKhau = Double.parseDouble(txtChietKhau.getText().trim());
            if (chietKhau < 0 || chietKhau > 100) {
                lblThanhToan.setText("CK 0-100%");
                return;
            }
            hoaDonHienTai.setChietKhau(chietKhau);
            double thanhToan = hoaDonHienTai.getTongThanhToan();
            lblThanhToan.setText(Formatter.money(thanhToan));

            String strTienDua = txtTienKhachDua.getText().trim();
            if (strTienDua.isEmpty()) {
                lblTienThua.setText("0 d");
                return;
            }
            double tienDua = Double.parseDouble(strTienDua);
            double tienThua = tienDua - thanhToan;
            lblTienThua.setText(Formatter.money(tienThua));
            lblTienThua.setForeground(tienThua < 0 ? UITheme.DANGER : UITheme.PRIMARY);
        } catch (NumberFormatException e) {
            lblThanhToan.setText("Nhập số hợp lệ");
        }
    }

    private void xemHoaDon() {
        if (hoaDonHienTai == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn order cần xem.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double chietKhau = parseChietKhau();
        if (chietKhau < 0) {
            return;
        }
        hoaDonHienTai.setChietKhau(chietKhau);
        String noiDung = taoNoiDungHoaDon(hoaDonHienTai);
        JTextArea area = new JTextArea(noiDung);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(380, 360));
        JOptionPane.showMessageDialog(this, sp,
                "Hóa đơn " + hoaDonHienTai.getMaHD(), JOptionPane.PLAIN_MESSAGE);
    }

    private void thanhToan() {
        if (hoaDonHienTai == null || hoaDonHienTai.getDanhSachChiTiet().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn order có món để thanh toán.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        double chietKhau = parseChietKhau();
        if (chietKhau < 0) {
            return;
        }
        double tienKhachDua;
        try {
            tienKhachDua = Double.parseDouble(txtTienKhachDua.getText().trim());
            if (tienKhachDua < 0) {
                JOptionPane.showMessageDialog(this, "Tiền khách đưa phải >= 0.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Tiền khách đưa không hợp lệ.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        hoaDonHienTai.setChietKhau(chietKhau);
        double tongThanhToan = hoaDonHienTai.getTongThanhToan();
        double tienThua = tienKhachDua - tongThanhToan;

        String chiTiet = taoNoiDungHoaDon(hoaDonHienTai)
                + "Tiền khách đưa: " + Formatter.money(tienKhachDua) + "\n"
                + "Tiền thừa: " + Formatter.money(tienThua) + "\n";
        int confirm = JOptionPane.showConfirmDialog(this,
                chiTiet + "\nXác nhận thanh toán?",
                "Hóa đơn " + hoaDonHienTai.getMaHD(),
                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            hoaDonService.thanhToan(hoaDonHienTai, chietKhau, tienKhachDua);
            JOptionPane.showMessageDialog(this,
                    "Thanh toán thành công!\n" +
                    "Tổng hàng: " + Formatter.money(hoaDonHienTai.getTongTien()) + "\n" +
                    "Chiết khấu: " + chietKhau + "%\n" +
                    "Thanh toán: " + Formatter.money(tongThanhToan) + "\n" +
                    "Tiền thừa: " + Formatter.money(tienThua) + "\n" +
                    "Bàn \"" + hoaDonHienTai.getTenBan() + "\" đã được giải phóng.",
                    "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
            lamMoi();
            thongBaoThayDoi();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Parse chiet khau, tra ve -1 (va da bao loi) neu khong hop le. */
    private double parseChietKhau() {
        try {
            double chietKhau = Double.parseDouble(txtChietKhau.getText().trim());
            if (chietKhau < 0 || chietKhau > 100) {
                JOptionPane.showMessageDialog(this, "Chiết khẩu phải trong khoảng 0-100%.",
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                return -1;
            }
            return chietKhau;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Chiết khấu không hợp lệ.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private String taoNoiDungHoaDon(HoaDon hd) {
        StringBuilder sb = new StringBuilder();
        sb.append("       NHÀ HÀNG - HÓA ĐƠN\n");
        sb.append("Mã HD: ").append(hd.getMaHD()).append("\n");
        sb.append("Bàn: ").append(hd.getTenBan()).append("\n");
        sb.append("Nhân viên: ").append(hd.getTenNV()).append("\n");
        sb.append("----------------------------------------\n");
        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            sb.append(String.format("%-18s x%-3d %14s%n",
                    ct.getTenMon(), ct.getSoLuong(), Formatter.money(ct.getThanhTien())));
        }
        sb.append("----------------------------------------\n");
        sb.append("Tổng hàng: ").append(Formatter.money(hd.getTongTien())).append("\n");
        if (hd.getChietKhau() > 0) {
            sb.append("Chiết khấu: ").append(hd.getChietKhau()).append("%\n");
        }
        sb.append("THANH TOÁN: ").append(Formatter.money(hd.getTongThanhToan())).append("\n");
        return sb.toString();
    }

    private void thongBaoThayDoi() {
        if (onThayDoi != null) {
            onThayDoi.run();
        }
    }
}
