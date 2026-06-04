package com.restaurant.ui;

import com.restaurant.model.TaiKhoan;
import com.restaurant.service.AuthService;
import com.restaurant.util.SessionManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Optional;

import javax.swing.JOptionPane;

/**
 * Man hinh dang nhap.
 */
public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();
    private final JTextField txtUsername = new JTextField(18);
    private final JPasswordField txtPassword = new JPasswordField(18);
    private final JCheckBox chkShowPass = new JCheckBox("Hiện mật khẩu");

    public LoginFrame() {
        setTitle("Đăng nhập - Quản lý nhà hàng");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 480);
        setMinimumSize(new Dimension(720, 440));
        setLocationRelativeTo(null);

        setContentPane(buildContent());
        getRootPane().setDefaultButton(null);
    }

    private JPanel buildContent() {
        JPanel root = new JPanel(new BorderLayout());

        // Ben trai: banner thuong hieu
        JPanel left = new JPanel(new GridBagLayout());
        left.setBackground(UITheme.PRIMARY);
        left.setPreferredSize(new Dimension(360, 0));
        JLabel banner = new JLabel("<html><div style='text-align:center;'>"
                + "QUẢN LÝ<br>NHÀ HÀNG</div></html>", SwingConstants.CENTER);
        banner.setForeground(Color.WHITE);
        banner.setFont(UITheme.FONT_TITLE.deriveFont(30f));
        left.add(banner);

        // Ben phai: form dang nhap
        JPanel right = new JPanel(new GridBagLayout());
        right.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Đăng nhập hệ thống");
        lblTitle.setFont(UITheme.FONT_TITLE);
        lblTitle.setForeground(UITheme.PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        right.add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.gridx = 0;
        right.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridy = 2;
        txtUsername.setFont(UITheme.FONT_NORMAL);
        right.add(txtUsername, gbc);

        gbc.gridy = 3;
        right.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridy = 4;
        txtPassword.setFont(UITheme.FONT_NORMAL);
        right.add(txtPassword, gbc);

        gbc.gridy = 5;
        chkShowPass.addActionListener(e ->
                txtPassword.setEchoChar(chkShowPass.isSelected() ? (char) 0 : '•'));
        right.add(chkShowPass, gbc);

        JButton btnLogin = new JButton("ĐĂNG NHẬP");
        btnLogin.setBackground(UITheme.PRIMARY);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFont(UITheme.FONT_BUTTON);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(0, 42));
        btnLogin.addActionListener(e -> dangNhap());
        gbc.gridy = 6;
        right.add(btnLogin, gbc);

        JLabel lblHint = new JLabel("<html><i>admin/admin (Quan ly) &middot; nv01/123 (Phuc vu) &middot; tn01/123 (Thu ngan)</i></html>");
        lblHint.setForeground(Color.GRAY);
        gbc.gridy = 7;
        right.add(lblHint, gbc);

        getRootPane().setDefaultButton(btnLogin);

        KeyAdapter enterToLogin = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    dangNhap();
                }
            }
        };
        txtUsername.addKeyListener(enterToLogin);
        txtPassword.addKeyListener(enterToLogin);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);
        return root;
    }

    private void dangNhap() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ thông tin.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Optional<TaiKhoan> result = authService.dangNhap(username, password);
        if (result.isPresent()) {
            SessionManager.setCurrentUser(result.get());
            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng.",
                    "Đăng nhập thất bại", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}
