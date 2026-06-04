package com.restaurant.ui.panel;

import com.restaurant.model.NhanVien;
import com.restaurant.model.enums.CaLamViec;
import com.restaurant.service.NhanVienService;
import com.restaurant.ui.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

/**
 * Quan ly thong tin nhan vien (chi danh cho Admin).
 */
public class NhanVienPanel extends JPanel {

    private final NhanVienService service = new NhanVienService();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã NV", "Họ tên", "Ca làm việc", "SDT", "Lương/ca"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField txtMa = new JTextField(10);
    private final JTextField txtTen = new JTextField(18);
    private final JComboBox<CaLamViec> cboCa = new JComboBox<>(CaLamViec.values());
    private final JTextField txtSdt = new JTextField(14);
    private final JTextField txtLuong = new JTextField(10);
    private final JTextField txtSearch = new JTextField(16);

    public NhanVienPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillForm();
            }
        });

        lamMoi();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel title = new JLabel("QUẢN LÝ NHÂN VIÊN");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        right.add(new JLabel("Tìm:"));
        txtSearch.addActionListener(e -> apDungLoc());
        right.add(txtSearch);
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> apDungLoc());
        right.add(btnTim);
        panel.add(right, BorderLayout.EAST);
        return panel;
    }

    private JScrollPane buildTable() {
        table.setRowHeight(28);
        table.setFont(UITheme.FONT_NORMAL);
        table.getTableHeader().setFont(UITheme.FONT_BUTTON);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return new JScrollPane(table);
    }

    private JPanel buildForm() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 8));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtMa.setEditable(false);
        txtMa.setBackground(new Color(0xEE, 0xEE, 0xEE));

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1; form.add(txtMa, gbc);
        gbc.gridx = 2; form.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 3; form.add(txtTen, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Ca làm việc:"), gbc);
        gbc.gridx = 1; form.add(cboCa, gbc);
        gbc.gridx = 2; form.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 3; form.add(txtSdt, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Lương/ca (VND):"), gbc);
        gbc.gridx = 1; form.add(txtLuong, gbc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnMoi = new JButton("Làm mới");
        JButton btnThem = new JButton("Thêm");
        JButton btnSua = new JButton("Cập nhật");
        JButton btnXoa = new JButton("Xóa");

        styleButton(btnThem, UITheme.PRIMARY);
        styleButton(btnSua, UITheme.ACCENT);
        styleButton(btnXoa, UITheme.DANGER);

        btnMoi.addActionListener(e -> clearForm());
        btnThem.addActionListener(e -> them());
        btnSua.addActionListener(e -> capNhat());
        btnXoa.addActionListener(e -> xoa());

        buttons.add(btnMoi);
        buttons.add(btnThem);
        buttons.add(btnSua);
        buttons.add(btnXoa);

        wrapper.add(form, BorderLayout.CENTER);
        wrapper.add(buttons, BorderLayout.SOUTH);
        return wrapper;
    }

    private void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(UITheme.FONT_BUTTON);
        btn.setPreferredSize(new Dimension(110, 34));
    }

    // ===== Logic =====

    public void lamMoi() {
        apDungLoc();
    }

    private void apDungLoc() {
        String kw = txtSearch.getText().trim();
        List<NhanVien> list = service.search(kw);
        tableModel.setRowCount(0);
        for (NhanVien nv : list) {
            tableModel.addRow(new Object[]{
                    nv.getMaNV(), nv.getTenNV(),
                    nv.getCaLamViec() != null ? nv.getCaLamViec().getMoTa() : "",
                    nv.getSdt(),
                    com.restaurant.util.Formatter.money(nv.getLuongTheoCa())
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String ma = tableModel.getValueAt(row, 0).toString();
        service.getAll().stream()
                .filter(nv -> nv.getMaNV().equals(ma))
                .findFirst()
                .ifPresent(nv -> {
                    txtMa.setText(nv.getMaNV());
                    txtTen.setText(nv.getTenNV());
                    cboCa.setSelectedItem(nv.getCaLamViec());
                    txtSdt.setText(nv.getSdt());
                    txtLuong.setText(String.valueOf((long) nv.getLuongTheoCa()));
                });
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        cboCa.setSelectedIndex(0);
        txtSdt.setText("");
        txtLuong.setText("");
        table.clearSelection();
        txtTen.requestFocus();
    }

    private void them() {
        Double luong = parseLuong();
        if (luong == null) return;
        try {
            service.them(txtTen.getText(), (CaLamViec) cboCa.getSelectedItem(), txtSdt.getText(), luong);
            lamMoi();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (txtMa.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên cần cập nhật.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Double luong = parseLuong();
        if (luong == null) return;
        try {
            service.capNhat(txtMa.getText(), txtTen.getText(),
                    (CaLamViec) cboCa.getSelectedItem(), txtSdt.getText(), luong);
            lamMoi();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Double parseLuong() {
        try {
            double luong = Double.parseDouble(txtLuong.getText().trim());
            if (luong <= 0) {
                throw new NumberFormatException();
            }
            return luong;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Lương phải là số dương (> 0).",
                    "Loi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void xoa() {
        if (txtMa.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn nhân viên cần xóa.",
                    "Thong bao", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa nhân viên \"" + txtTen.getText() + "\"?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.xoa(txtMa.getText());
            lamMoi();
            clearForm();
        }
    }
}
