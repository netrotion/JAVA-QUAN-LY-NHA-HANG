package com.restaurant.ui.panel;

import com.restaurant.model.ChiTietHoaDon;
import com.restaurant.model.HoaDon;
import com.restaurant.service.HoaDonService;
import com.restaurant.ui.UITheme;
import com.restaurant.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Tra cuu hoa don theo ma hoa don hoac ma ban.
 * Hien thi danh sach hoa don + chi tiet mon, tom tat tien (gom tien khach dua / tien thua
 * da luu khi thanh toan) va cho phep IN hoa don ra may in / PDF.
 */
public class HoaDonPanel extends JPanel {

    private final HoaDonService service = new HoaDonService();

    private final JTextField txtTimKiem = new JTextField(20);

    private final DefaultTableModel hdModel = new DefaultTableModel(
            new Object[]{"Mã HD", "Bàn", "Nhân viên", "Thời gian", "Tổng tiền", "Trạng thái"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable hdTable = new JTable(hdModel);

    private final DefaultTableModel ctModel = new DefaultTableModel(
            new Object[]{"Tên món", "SL", "Đơn giá", "Thành tiền"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable ctTable = new JTable(ctModel);

    // Tom tat tien cua hoa don dang chon
    private final JLabel lblTongHang = new JLabel("0 d");
    private final JLabel lblChietKhau = new JLabel("0 %");
    private final JLabel lblThanhTien = new JLabel("0 d");
    private final JLabel lblTienKhachDua = new JLabel("0 d");
    private final JLabel lblTienThua = new JLabel("0 d");
    private final JButton btnIn = new JButton("In hóa đơn");

    private HoaDon hoaDonDangChon;

    public HoaDonPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildTop(), buildBottom());
        split.setResizeWeight(0.55);
        split.setDividerLocation(320);
        add(split, BorderLayout.CENTER);

        hdTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiChiTiet();
            }
        });

        lamMoi();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("TRA CỨU / IN HÓA ĐƠN");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JPanel search = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        search.add(new JLabel("Tìm theo Mã HD / Mã bàn:"));
        txtTimKiem.addActionListener(e -> timKiem());
        search.add(txtTimKiem);
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> timKiem());
        search.add(btnTim);
        JButton btnTatCa = new JButton("Tất cả");
        btnTatCa.addActionListener(e -> lamMoi());
        search.add(btnTatCa);
        panel.add(search, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildTop() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        hdTable.setRowHeight(28);
        hdTable.setFont(UITheme.FONT_NORMAL);
        hdTable.getTableHeader().setFont(UITheme.FONT_BUTTON);
        hdTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(hdTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottom() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createTitledBorder("Chi tiết hóa đơn"));
        ctTable.setRowHeight(28);
        ctTable.setFont(UITheme.FONT_NORMAL);
        ctTable.getTableHeader().setFont(UITheme.FONT_BUTTON);
        panel.add(new JScrollPane(ctTable), BorderLayout.CENTER);

        panel.add(buildTomTat(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildTomTat() {
        JPanel wrap = new JPanel(new BorderLayout(12, 0));

        JPanel grid = new JPanel(new GridLayout(0, 2, 24, 2));
        grid.setBorder(BorderFactory.createEmptyBorder(6, 4, 6, 4));
        grid.add(dongTomTat("Tổng hàng:", lblTongHang, false));
        grid.add(dongTomTat("Tiền khách đưa:", lblTienKhachDua, false));
        grid.add(dongTomTat("Chiết khấu:", lblChietKhau, false));
        grid.add(dongTomTat("Tiền thừa:", lblTienThua, false));
        grid.add(dongTomTat("THANH TOÁN:", lblThanhTien, true));
        wrap.add(grid, BorderLayout.CENTER);

        btnIn.setBackground(UITheme.PRIMARY);
        btnIn.setForeground(Color.WHITE);
        btnIn.setFont(UITheme.FONT_BUTTON);
        btnIn.setFocusPainted(false);
        btnIn.addActionListener(e -> inHoaDon());
        JPanel inWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        inWrap.add(btnIn);
        wrap.add(inWrap, BorderLayout.EAST);

        return wrap;
    }

    private JPanel dongTomTat(String nhan, JLabel giaTri, boolean noiBat) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JLabel l = new JLabel(nhan);
        if (noiBat) {
            l.setFont(UITheme.FONT_HEADER);
            giaTri.setFont(new Font("Segoe UI", Font.BOLD, 16));
            giaTri.setForeground(UITheme.DANGER);
        } else {
            giaTri.setFont(UITheme.FONT_NORMAL);
        }
        p.add(l);
        p.add(giaTri);
        return p;
    }

    // ===== Logic =====

    public void lamMoi() {
        txtTimKiem.setText("");
        hienThiDanhSach(service.getAll());
    }

    private void timKiem() {
        String keyword = txtTimKiem.getText().trim();
        if (keyword.isEmpty()) {
            lamMoi();
            return;
        }
        String kw = keyword.toLowerCase();
        List<HoaDon> result = service.getAll().stream()
                .filter(hd -> hd.getMaHD().toLowerCase().contains(kw)
                        || hd.getMaBan().toLowerCase().contains(kw))
                .collect(Collectors.toList());
        hienThiDanhSach(result);
    }

    private void hienThiDanhSach(List<HoaDon> list) {
        hdModel.setRowCount(0);
        for (HoaDon hd : list) {
            hdModel.addRow(new Object[]{
                    hd.getMaHD(),
                    hd.getTenBan(),
                    hd.getTenNV(),
                    hd.getThoiGianThanhToan() != null
                            ? Formatter.dateTime(hd.getThoiGianThanhToan())
                            : Formatter.dateTime(hd.getThoiGianTao()),
                    Formatter.money(hd.getTongThanhToan()),
                    hd.getTrangThai().getMoTa()
            });
        }
        ctModel.setRowCount(0);
        xoaTomTat();
    }

    private void hienThiChiTiet() {
        int row = hdTable.getSelectedRow();
        if (row < 0) {
            ctModel.setRowCount(0);
            xoaTomTat();
            return;
        }
        String maHD = hdModel.getValueAt(row, 0).toString();
        service.getAll().stream()
                .filter(hd -> hd.getMaHD().equals(maHD))
                .findFirst()
                .ifPresent(hd -> {
                    hoaDonDangChon = hd;
                    ctModel.setRowCount(0);
                    for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
                        ctModel.addRow(new Object[]{
                                ct.getTenMon(),
                                ct.getSoLuong(),
                                Formatter.money(ct.getDonGia()),
                                Formatter.money(ct.getThanhTien())
                        });
                    }
                    capNhatTomTat(hd);
                });
    }

    private void capNhatTomTat(HoaDon hd) {
        lblTongHang.setText(Formatter.money(hd.getTongTien()));
        lblChietKhau.setText(Formatter.number(hd.getChietKhau()) + " %");
        lblThanhTien.setText(Formatter.money(hd.getTongThanhToan()));
        lblTienKhachDua.setText(Formatter.money(hd.getTienKhachDua()));
        lblTienThua.setText(Formatter.money(hd.getTienThua()));
        btnIn.setEnabled(true);
    }

    private void xoaTomTat() {
        hoaDonDangChon = null;
        lblTongHang.setText("0 d");
        lblChietKhau.setText("0 %");
        lblThanhTien.setText("0 d");
        lblTienKhachDua.setText("0 d");
        lblTienThua.setText("0 d");
        btnIn.setEnabled(false);
    }

    /**
     * Hien hoa don dang chon trong mot cua so moi (tam thoi thay cho in may in).
     */
    private void inHoaDon() {
        if (hoaDonDangChon == null) {
            JOptionPane.showMessageDialog(this, "Hãy chọn hóa đơn cần in.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JTextArea area = new JTextArea(taoNoiDungHoaDon(hoaDonDangChon));
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setEditable(false);
        area.setMargin(new Insets(12, 16, 12, 16));

        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new Dimension(360, 480));

        JDialog dialog = new JDialog(
                SwingUtilities.getWindowAncestor(this) instanceof Frame f ? f : null,
                "Hóa đơn " + hoaDonDangChon.getMaHD(),
                ModalityType.APPLICATION_MODAL);
        dialog.setLayout(new BorderLayout(0, 8));
        dialog.add(scroll, BorderLayout.CENTER);

        JButton btnDong = new JButton("Đóng");
        btnDong.addActionListener(e -> dialog.dispose());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        south.add(btnDong);
        dialog.add(south, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    /**
     * Tao noi dung hoa don dang van ban de in (gom tien khach dua / tien thua da luu).
     */
    private String taoNoiDungHoaDon(HoaDon hd) {
        StringBuilder sb = new StringBuilder();
        sb.append("            NHÀ HÀNG\n");
        sb.append("           HÓA ĐƠN BÁN HÀNG\n");
        sb.append("========================================\n");
        sb.append("Mã HD     : ").append(hd.getMaHD()).append("\n");
        sb.append("Bàn       : ").append(hd.getTenBan()).append("\n");
        sb.append("Nhân viên : ").append(hd.getTenNV()).append("\n");
        sb.append("Thời gian : ").append(hd.getThoiGianThanhToan() != null
                ? Formatter.dateTime(hd.getThoiGianThanhToan())
                : Formatter.dateTime(hd.getThoiGianTao())).append("\n");
        sb.append("Trạng thái: ").append(hd.getTrangThai().getMoTa()).append("\n");
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-18s %3s %14s%n", "Tên món", "SL", "Thành tiền"));
        sb.append("----------------------------------------\n");
        for (ChiTietHoaDon ct : hd.getDanhSachChiTiet()) {
            sb.append(String.format("%-18s %3d %14s%n",
                    ct.getTenMon(), ct.getSoLuong(), Formatter.money(ct.getThanhTien())));
        }
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-22s %14s%n", "Tổng hàng:", Formatter.money(hd.getTongTien())));
        if (hd.getChietKhau() > 0) {
            sb.append(String.format("%-22s %14s%n", "Chiết khấu:", Formatter.number(hd.getChietKhau()) + " %"));
        }
        sb.append(String.format("%-22s %14s%n", "THANH TOÁN:", Formatter.money(hd.getTongThanhToan())));
        sb.append(String.format("%-22s %14s%n", "Tiền khách đưa:", Formatter.money(hd.getTienKhachDua())));
        sb.append(String.format("%-22s %14s%n", "Tiền thừa:", Formatter.money(hd.getTienThua())));
        sb.append("========================================\n");
        sb.append("        Cảm ơn quý khách!\n");
        return sb.toString();
    }
}
