package com.restaurant.ui.panel;

import com.restaurant.service.HoaDonService;
import com.restaurant.ui.UITheme;
import com.restaurant.util.Formatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

/**
 * Bao cao doanh thu va top mon ban chay (chi Admin).
 * Loc theo: Hom nay / Thang nay / Nam nay / Khoang ngay tuy chinh.
 */
public class ThongKePanel extends JPanel {

    private final HoaDonService service = new HoaDonService();

    private final JComboBox<String> cboKy = new JComboBox<>(new String[]{
            "Hôm nay", "Tháng này", "Năm nay", "Tùy chỉnh"
    });
    private final JTextField txtTuNgay = new JTextField("01/01/2026", 10);
    private final JTextField txtDenNgay = new JTextField("31/12/2026", 10);
    private final JLabel lblTongHD = new JLabel("0");
    private final JLabel lblDoanhThu = new JLabel("0 d");

    private final DefaultTableModel topModel = new DefaultTableModel(
            new Object[]{"STT", "Tên món", "Số lượng bàn"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable topTable = new JTable(topModel);

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildSummary(), BorderLayout.CENTER);
        add(buildTop(), BorderLayout.SOUTH);

        cboKy.addActionListener(e -> {
            boolean tuychinh = "Tùy chỉnh".equals(cboKy.getSelectedItem());
            txtTuNgay.setEnabled(tuychinh);
            txtDenNgay.setEnabled(tuychinh);
        });
        txtTuNgay.setEnabled(false);
        txtDenNgay.setEnabled(false);

        thongKe();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("BÁO CÁO DOANH THU");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filter.add(new JLabel("Kỳ thống kê:"));
        filter.add(cboKy);
        filter.add(new JLabel("Từ ngày (dd/MM/yyyy):"));
        filter.add(txtTuNgay);
        filter.add(new JLabel("Đến ngày:"));
        filter.add(txtDenNgay);
        JButton btnThongKe = new JButton("Thống kê");
        btnThongKe.setBackground(UITheme.PRIMARY);
        btnThongKe.setForeground(Color.WHITE);
        btnThongKe.setFocusPainted(false);
        btnThongKe.addActionListener(e -> thongKe());
        filter.add(btnThongKe);
        panel.add(filter, BorderLayout.EAST);

        return panel;
    }

    private JPanel buildSummary() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 16, 0));
        panel.setBorder(BorderFactory.createTitledBorder("Tổng kết"));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 20));
        left.add(new JLabel("Tổng số hóa đơn:"));
        lblTongHD.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTongHD.setForeground(UITheme.PRIMARY);
        left.add(lblTongHD);
        panel.add(left);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 20));
        right.add(new JLabel("Tổng doanh thu:"));
        lblDoanhThu.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblDoanhThu.setForeground(UITheme.DANGER);
        right.add(lblDoanhThu);
        panel.add(right);

        return panel;
    }

    private JPanel buildTop() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Top 10 món bán chạy nhất"));
        topTable.setRowHeight(28);
        topTable.setFont(UITheme.FONT_NORMAL);
        topTable.getTableHeader().setFont(UITheme.FONT_BUTTON);
        panel.add(new JScrollPane(topTable), BorderLayout.CENTER);
        return panel;
    }

    // ===== Logic =====

    private void thongKe() {
        String ky = (String) cboKy.getSelectedItem();
        LocalDateTime tuNgay = null;
        LocalDateTime denNgay = null;

        LocalDate today = LocalDate.now();
        switch (ky) {
            case "Hôm nay" -> {
                tuNgay = today.atStartOfDay();
                denNgay = today.atTime(LocalTime.MAX);
            }
            case "Tháng này" -> {
                tuNgay = today.withDayOfMonth(1).atStartOfDay();
                denNgay = today.withDayOfMonth(today.lengthOfMonth()).atTime(LocalTime.MAX);
            }
            case "Năm nay" -> {
                tuNgay = today.withDayOfYear(1).atStartOfDay();
                denNgay = today.withDayOfYear(today.lengthOfYear()).atTime(LocalTime.MAX);
            }
            case "Tùy chỉnh" -> {
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                try {
                    tuNgay = LocalDate.parse(txtTuNgay.getText().trim(), fmt).atStartOfDay();
                    denNgay = LocalDate.parse(txtDenNgay.getText().trim(), fmt).atTime(LocalTime.MAX);
                } catch (DateTimeParseException e) {
                    javax.swing.JOptionPane.showMessageDialog(this,
                            "Định dạng ngày không hợp lệ. Dùng dd/MM/yyyy.",
                            "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }

        List<com.restaurant.model.HoaDon> dsHD = service.locTheoThoiGian(tuNgay, denNgay);
        lblTongHD.setText(String.valueOf(dsHD.size()));
        lblDoanhThu.setText(Formatter.money(service.tongDoanhThuTheoKy(tuNgay, denNgay)));

        // Top mon ban chay
        List<Map.Entry<String, Integer>> top = service.topMonBanChay(tuNgay, denNgay, 10);
        topModel.setRowCount(0);
        int stt = 1;
        for (Map.Entry<String, Integer> entry : top) {
            topModel.addRow(new Object[]{stt++, entry.getKey(), entry.getValue()});
        }
    }
}
