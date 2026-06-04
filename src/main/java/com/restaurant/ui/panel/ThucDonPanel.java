package com.restaurant.ui.panel;

import com.restaurant.model.MonAn;
import com.restaurant.service.MonAnService;
import com.restaurant.ui.UITheme;
import com.restaurant.util.Formatter;

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
 * Quan ly thuc don: them / sua / xoa mon an, loc theo danh muc, tim kiem.
 */
public class ThucDonPanel extends JPanel {

    private final MonAnService service = new MonAnService();

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã món", "Tên món", "Danh mục", "Đơn giá"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);

    private final JTextField txtMa = new JTextField(10);
    private final JTextField txtTen = new JTextField(18);
    private final JTextField txtDanhMuc = new JTextField(14);
    private final JTextField txtDonGia = new JTextField(10);

    private final JTextField txtSearch = new JTextField(16);
    private final JComboBox<String> cboLocDanhMuc = new JComboBox<>();

    public ThucDonPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildForm(), BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelection();
            }
        });

        lamMoi();
    }

    private JPanel buildHeader() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel title = new JLabel("QUẢN LÝ THỰC ĐƠN");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY_DARK);
        panel.add(title, BorderLayout.WEST);

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        filter.add(new JLabel("Danh mục:"));
        cboLocDanhMuc.addActionListener(e -> apDungLoc());
        filter.add(cboLocDanhMuc);
        filter.add(new JLabel("Tìm:"));
        txtSearch.addActionListener(e -> apDungLoc());
        filter.add(txtSearch);
        JButton btnTim = new JButton("Tìm");
        btnTim.addActionListener(e -> apDungLoc());
        filter.add(btnTim);
        panel.add(filter, BorderLayout.EAST);

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
        form.setBorder(BorderFactory.createTitledBorder("Thông tin món ăn"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.anchor = GridBagConstraints.WEST;

        txtMa.setEditable(false);
        txtMa.setBackground(new Color(0xEE, 0xEE, 0xEE));

        gbc.gridx = 0; gbc.gridy = 0; form.add(new JLabel("Mã món:"), gbc);
        gbc.gridx = 1; form.add(txtMa, gbc);
        gbc.gridx = 2; form.add(new JLabel("Tên món:"), gbc);
        gbc.gridx = 3; form.add(txtTen, gbc);

        gbc.gridx = 0; gbc.gridy = 1; form.add(new JLabel("Danh mục:"), gbc);
        gbc.gridx = 1; form.add(txtDanhMuc, gbc);
        gbc.gridx = 2; form.add(new JLabel("Đơn giá:"), gbc);
        gbc.gridx = 3; form.add(txtDonGia, gbc);

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
        capNhatComboLoc();
        apDungLoc();
    }

    private void capNhatComboLoc() {
        Object dangChon = cboLocDanhMuc.getSelectedItem();
        cboLocDanhMuc.removeAllItems();
        cboLocDanhMuc.addItem("Tất cả");
        for (String dm : service.getDanhSachDanhMuc()) {
            cboLocDanhMuc.addItem(dm);
        }
        if (dangChon != null) {
            cboLocDanhMuc.setSelectedItem(dangChon);
        }
    }

    private void apDungLoc() {
        String danhMuc = (String) cboLocDanhMuc.getSelectedItem();
        String keyword = txtSearch.getText().trim();

        List<MonAn> list = service.getByDanhMuc(danhMuc);
        if (!keyword.isEmpty()) {
            String kw = keyword.toLowerCase();
            list.removeIf(m -> !(m.getTenMon().toLowerCase().contains(kw)
                    || m.getMaMon().toLowerCase().contains(kw)));
        }

        tableModel.setRowCount(0);
        for (MonAn m : list) {
            tableModel.addRow(new Object[]{
                    m.getMaMon(), m.getTenMon(), m.getDanhMuc(), Formatter.money(m.getDonGia())
            });
        }
    }

    private void fillFormFromSelection() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        String maMon = tableModel.getValueAt(row, 0).toString();
        service.getAll().stream()
                .filter(m -> m.getMaMon().equals(maMon))
                .findFirst()
                .ifPresent(m -> {
                    txtMa.setText(m.getMaMon());
                    txtTen.setText(m.getTenMon());
                    txtDanhMuc.setText(m.getDanhMuc());
                    txtDonGia.setText(String.valueOf((long) m.getDonGia()));
                });
    }

    private void clearForm() {
        txtMa.setText("");
        txtTen.setText("");
        txtDanhMuc.setText("");
        txtDonGia.setText("");
        table.clearSelection();
        txtTen.requestFocus();
    }

    private Double parseDonGia() {
        try {
            double gia = Double.parseDouble(txtDonGia.getText().trim());
            if (gia < 0) {
                throw new NumberFormatException();
            }
            return gia;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Đơn giá phải là số không âm.",
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void them() {
        Double gia = parseDonGia();
        if (gia == null) return;
        try {
            service.them(txtTen.getText(), txtDanhMuc.getText().trim(), gia);
            lamMoi();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm món thành công.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhat() {
        if (txtMa.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn món cần cập nhật.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Double gia = parseDonGia();
        if (gia == null) return;
        try {
            service.capNhat(txtMa.getText(), txtTen.getText(), txtDanhMuc.getText().trim(), gia);
            lamMoi();
            JOptionPane.showMessageDialog(this, "Cập nhật thành công.");
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoa() {
        if (txtMa.getText().isBlank()) {
            JOptionPane.showMessageDialog(this, "Hãy chọn món cần xóa.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xóa món \"" + txtTen.getText() + "\"?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            service.xoa(txtMa.getText());
            lamMoi();
            clearForm();
        }
    }
}
