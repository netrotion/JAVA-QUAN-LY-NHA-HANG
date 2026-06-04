package com.restaurant.ui.panel;

import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;
import com.restaurant.model.MonAn;
import com.restaurant.model.Ban;
import com.restaurant.service.BanService;
import com.restaurant.service.HoaDonService;
import com.restaurant.service.MonAnService;
import com.restaurant.ui.UITheme;
import com.restaurant.util.Formatter;
import com.restaurant.util.SessionManager;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.List;
import java.util.Optional;

/**
 * UC4 Tao order / UC5 Cap nhat order - danh cho NHAN VIEN PHUC VU va QUAN LY (ADMIN).
 * Trai: chon ban + danh sach mon de them.
 * Phai: chi tiet order dang mo cua ban + nut sua so luong / xoa mon / huy.
 * KHONG co chuc nang thanh toan (do Thu ngan dam nhan o man hinh rieng).
 */
public class OrderPanel extends JPanel {

    private final BanService banService = new BanService();
    private final MonAnService monAnService = new MonAnService();
    private final HoaDonService hoaDonService = new HoaDonService();

    private final JComboBox<Ban> cboBan = new JComboBox<>();
    private final JTextField txtTimMon = new JTextField(14);

    private final DefaultTableModel monModel = new DefaultTableModel(
            new Object[]{"Ma", "Ten mon", "Danh muc", "Don gia"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable monTable = new JTable(monModel);
    private final JSpinner spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 99, 1));

    private final DefaultTableModel hdModel = new DefaultTableModel(
            new Object[]{"Mã món", "Tên món", "SL", "Đơn giá", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable hdTable = new JTable(hdModel);
    private final JLabel lblTamTinh = new JLabel("0 d");

    private HoaDon hoaDonHienTai;
    private Runnable onThayDoi;

    public OrderPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeft(), buildRight());
        split.setResizeWeight(0.5);
        split.setDividerLocation(560);
        add(split, BorderLayout.CENTER);

        cboBan.addActionListener(e -> onChonBan());
        lamMoi();
    }

    public void setOnThayDoi(Runnable onThayDoi) {
        this.onThayDoi = onThayDoi;
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("TẠO / CẬP NHẬT ORDER");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(new JLabel("Bàn:"));
        cboBan.setPreferredSize(new Dimension(180, 30));
        right.add(cboBan);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JPanel buildLeft() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Thực đơn - chọn món để thêm"));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        top.add(new JLabel("Tìm món:"));
        txtTimMon.addActionListener(e -> loadMon());
        top.add(txtTimMon);
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> loadMon());
        top.add(btnTim);
        panel.add(top, BorderLayout.NORTH);

        monTable.setRowHeight(26);
        monTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(monTable), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        bottom.add(new JLabel("Số lượng:"));
        spnSoLuong.setPreferredSize(new Dimension(60, 30));
        bottom.add(spnSoLuong);
        JButton btnThem = new JButton("Thêm vào order  >>");
        btnThem.setBackground(UITheme.PRIMARY);
        btnThem.setForeground(Color.WHITE);
        btnThem.setFocusPainted(false);
        btnThem.setFont(UITheme.FONT_BUTTON);
        btnThem.addActionListener(e -> themMonVaoHoaDon());
        bottom.add(btnThem);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel buildRight() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Order hiện tại"));

        hdTable.setRowHeight(26);
        hdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(hdTable), BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 8));

        JPanel tongPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JLabel lbl = new JLabel("TẠM TÍNH: ");
        lbl.setFont(UITheme.FONT_HEADER);
        lblTamTinh.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTamTinh.setForeground(UITheme.DANGER);
        tongPanel.add(lbl);
        tongPanel.add(lblTamTinh);
        south.add(tongPanel, BorderLayout.NORTH);

        JLabel ghiChu = new JLabel("Khách hàng thanh toán tại quầy thu ngân.");
        ghiChu.setForeground(Color.GRAY);
        ghiChu.setHorizontalAlignment(JLabel.RIGHT);
        south.add(ghiChu, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 8, 0));
        JButton btnDoiSL = new JButton("Đổi số lượng");
        JButton btnXoaMon = new JButton("Xóa món chọn");
        JButton btnHuy = new JButton("Hủy order");

        btnDoiSL.addActionListener(e -> doiSoLuong());
        btnXoaMon.addActionListener(e -> xoaMonChon());
        btnHuy.addActionListener(e -> huyHoaDon());

        btnDoiSL.setBackground(UITheme.ACCENT);
        btnDoiSL.setForeground(Color.WHITE);
        btnDoiSL.setFocusPainted(false);
        btnHuy.setBackground(UITheme.DANGER);
        btnHuy.setForeground(Color.WHITE);
        btnHuy.setFocusPainted(false);

        buttons.add(btnDoiSL);
        buttons.add(btnXoaMon);
        buttons.add(btnHuy);
        south.add(buttons, BorderLayout.SOUTH);

        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ===== Logic =====

    /**
     * Nap lai toan bo: danh sach ban vao combo, danh sach mon, va order dang chon.
     */
    public void lamMoi() {
        Ban dangChon = (Ban) cboBan.getSelectedItem();
        List<Ban> dsBan = banService.getAll();
        cboBan.setModel(new DefaultComboBoxModel<>(dsBan.toArray(new Ban[0])));
        if (dangChon != null) {
            for (int i = 0; i < cboBan.getItemCount(); i++) {
                if (cboBan.getItemAt(i).getMaBan().equals(dangChon.getMaBan())) {
                    cboBan.setSelectedIndex(i);
                    break;
                }
            }
        }
        loadMon();
        onChonBan();
    }

    /**
     * Mo mot ban cu the (goi tu so do ban).
     * @param maBan
     */
    public void moBan(String maBan) {
        lamMoi();
        for (int i = 0; i < cboBan.getItemCount(); i++) {
            if (cboBan.getItemAt(i).getMaBan().equals(maBan)) {
                cboBan.setSelectedIndex(i);
                break;
            }
        }
        onChonBan();
    }

    private void loadMon() {
        String kw = txtTimMon.getText().trim();
        List<MonAn> list = monAnService.search(kw);
        monModel.setRowCount(0);
        for (MonAn m : list) {
            monModel.addRow(new Object[]{
                    m.getMaMon(), m.getTenMon(), m.getDanhMuc(), Formatter.money(m.getDonGia())
            });
        }
    }

    private void onChonBan() {
        Ban ban = (Ban) cboBan.getSelectedItem();
        if (ban == null) {
            hoaDonHienTai = null;
            hdModel.setRowCount(0);
            capNhatTongTien();
            return;
        }
        Optional<HoaDon> dangMo = hoaDonService.getHoaDonDangMo(ban.getMaBan());
        hoaDonHienTai = dangMo.orElse(null);
        capNhatBangHoaDon();
    }

    private void capNhatBangHoaDon() {
        hdModel.setRowCount(0);
        if (hoaDonHienTai != null) {
            for (ChiTietHoaDon ct : hoaDonHienTai.getDanhSachChiTiet()) {
                hdModel.addRow(new Object[]{
                        ct.getMaMon(), ct.getTenMon(), ct.getSoLuong(),
                        Formatter.money(ct.getDonGia()), Formatter.money(ct.getThanhTien())
                });
            }
        }
        capNhatTongTien();
    }

    private void capNhatTongTien() {
        double tong = hoaDonHienTai != null ? hoaDonHienTai.getTongTien() : 0;
        lblTamTinh.setText(Formatter.money(tong));
    }

    private void themMonVaoHoaDon() {
        Ban ban = (Ban) cboBan.getSelectedItem();
        if (ban == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn bàn trước.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int row = monTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn món cần thêm.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maMon = monModel.getValueAt(row, 0).toString();
        MonAn monAn = monAnService.getAll().stream()
                .filter(m -> m.getMaMon().equals(maMon))
                .findFirst().orElse(null);
        if (monAn == null) {
            return;
        }
        int soLuong = (Integer) spnSoLuong.getValue();

        // Mo order neu chua co
        if (hoaDonHienTai == null) {
            hoaDonHienTai = hoaDonService.moHoaDon(
                    ban.getMaBan(), ban.getTenBan(), SessionManager.getCurrentUser());
        }
        hoaDonService.themMon(hoaDonHienTai, monAn, soLuong);
        capNhatBangHoaDon();
        thongBaoThayDoi();
    }

    /**
     * UC5 - doi so luong cua mon dang chon trong order.
     */
    private void doiSoLuong() {
        if (hoaDonHienTai == null) {
            return;
        }
        int row = hdTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn món trong order để đổi số lượng.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maMon = hdModel.getValueAt(row, 0).toString();
        int slHienTai = Integer.parseInt(hdModel.getValueAt(row, 2).toString());
        String input = JOptionPane.showInputDialog(this,
                "Số lượng mới cho \"" + hdModel.getValueAt(row, 1) + "\":",
                slHienTai);
        if (input == null) {
            return;
        }
        try {
            int slMoi = Integer.parseInt(input.trim());
            if (slMoi < 0) {
                JOptionPane.showMessageDialog(this, "Số lượng phải >= 0.",
                        "Loi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            hoaDonService.capNhatSoLuong(hoaDonHienTai, maMon, slMoi);
            // Neu xoa het mon -> order co the rong, cap nhat lai trang thai
            if (hoaDonHienTai.getDanhSachChiTiet().isEmpty()) {
                onChonBan();
            } else {
                capNhatBangHoaDon();
            }
            thongBaoThayDoi();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaMonChon() {
        if (hoaDonHienTai == null) {
            return;
        }
        int row = hdTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn món trong order để xóa.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maMon = hdModel.getValueAt(row, 0).toString();
        hoaDonService.xoaMon(hoaDonHienTai, maMon);
        capNhatBangHoaDon();
        thongBaoThayDoi();
    }

    private void huyHoaDon() {
        if (hoaDonHienTai == null) {
            JOptionPane.showMessageDialog(this, "Bàn này không có order đang mở.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Hủy order " + hoaDonHienTai.getMaHD() + "? Bàn sẽ được trả về trống.",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            hoaDonService.huyHoaDon(hoaDonHienTai);
            hoaDonHienTai = null;
            capNhatBangHoaDon();
            thongBaoThayDoi();
        }
    }

    private void thongBaoThayDoi() {
        if (onThayDoi != null) {
            onThayDoi.run();
        }
    }
}
