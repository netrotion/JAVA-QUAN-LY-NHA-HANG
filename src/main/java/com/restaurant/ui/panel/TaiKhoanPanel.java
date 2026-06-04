package com.restaurant.ui.panel;

import com.restaurant.model.NhanVien;
import com.restaurant.model.TaiKhoan;
import com.restaurant.model.enums.VaiTro;
import com.restaurant.service.AuthService;
import com.restaurant.service.NhanVienService;
import com.restaurant.ui.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
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
 * Quan ly tai khoan dang nhap (chi danh cho Admin).
 */
public class TaiKhoanPanel extends JPanel {

    private final AuthService authService = new AuthService();
    private final NhanVienService nhanVienService = new NhanVienService();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Username", "Vai trò", "Mã NV", "Tên hiển thị"}, 0) {
        @Override
        public boolean isCellEditable(int r, int c) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField txtUsername = new JTextField(14);
    private final JPasswordField txtPassword = new JPasswordField(14);
    private final JComboBox<VaiTro> cboVaiTro = new JComboBox<>(VaiTro.values());
    private final JComboBox<String> cboMaNV = new JComboBox<>();
    private final JTextField txtTenHienThi = new JTextField(16);

    private boolean dangSua = false;

    public TaiKhoanPanel() {
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
        JLabel title = new JLabel("QUẢN LÝ TÀI KHOẢN");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(title, BorderLayout.WEST);
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
        form.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; form.add(txtUsername, gbc);
        gbc.gridx = 2; form.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 3; form.add(txtPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1; form.add(cboVaiTro, gbc);
        gbc.gridx = 2; form.add(new JLabel("Mã NV (nếu là NV):"), gbc);
        gbc.gridx = 3; form.add(cboMaNV, gbc);

        gbc.gridx = 0; gbc.gridy = 2; form.add(new JLabel("Tên hiển thị:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; form.add(txtTenHienThi, gbc);
        gbc.gridwidth = 1;

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
        // Nap danh sach ma NV vao combo
        cboMaNV.removeAllItems();
        cboMaNV.addItem("");
        for (NhanVien nv : nhanVienService.getAll()) {
            cboMaNV.addItem(nv.getMaNV());
        }

        tableModel.setRowCount(0);
        List<TaiKhoan> list = authService.getAll();
        for (TaiKhoan tk : list) {
            tableModel.addRow(new Object[]{
                    tk.getUsername(),
                    tk.getVaiTro() != null ? tk.getVaiTro().getMoTa() : "",
                    tk.getMaNV(),
                    tk.getTenHienThi()
            });
        }
    }

    private void fillForm() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String username = tableModel.getValueAt(row, 0).toString();
        authService.getAll().stream()
                .filter(tk -> tk.getUsername().equals(username))
                .findFirst()
                .ifPresent(tk -> {
                    txtUsername.setText(tk.getUsername());
                    txtUsername.setEditable(false);
                    txtPassword.setText("");
                    cboVaiTro.setSelectedItem(tk.getVaiTro());
                    cboMaNV.setSelectedItem(tk.getMaNV() != null ? tk.getMaNV() : "");
                    txtTenHienThi.setText(tk.getTenHienThi());
                    dangSua = true;
                });
    }

    private void clearForm() {
        txtUsername.setText("");
        txtUsername.setEditable(true);
        txtPassword.setText("");
        cboVaiTro.setSelectedIndex(0);
        cboMaNV.setSelectedIndex(0);
        txtTenHienThi.setText("");
        table.clearSelection();
        dangSua = false;
        txtUsername.requestFocus();
    }

    private String selectedMaNV() {
        Object v = cboMaNV.getSelectedItem();
        return v != null ? v.toString() : "";
    }

    private void them() {
        try {
            authService.them(
                    txtUsername.getText(),
                    new String(txtPassword.getPassword()),
                    (VaiTro) cboVaiTro.getSelectedItem(),
                    selectedMaNV(),
                    txtTenHienThi.getText().trim());
            lamMoi();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (txtUsername.getText().isBlank() || !dangSua) {
            JOptionPane.showMessageDialog(this, "Hãy chọn tài khoản cần cập nhật.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            authService.capNhat(
                    txtUsername.getText(),
                    new String(txtPassword.getPassword()),
                    (VaiTro) cboVaiTro.getSelectedItem(),
                    selectedMaNV(),
                    txtTenHienThi.getText().trim());
            lamMoi();
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thành công." +
                            (new String(txtPassword.getPassword()).isBlank()
                                    ? " (Mật khẩu giữ nguyên)" : ""));
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        if (txtUsername.getText().isBlank() || !dangSua) {
            JOptionPane.showMessageDialog(this, "Hãy chọn tài khoản cần xóa.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String username = txtUsername.getText();
        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản admin gốc.",
                    "Không cho phép", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa tài khoản \"" + username + "\"?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            authService.xoa(username);
            lamMoi();
            clearForm();
        }
    }
}
