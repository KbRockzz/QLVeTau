package com.trainstation.gui;

import com.trainstation.service.TaiKhoanService;
import com.trainstation.model.TaiKhoan;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Form đăng nhập
 */
public class FrmDangNhap extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private TaiKhoanService taiKhoanService;

    public FrmDangNhap() {
        taiKhoanService = TaiKhoanService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Make login dialog larger so scaled UI looks comfortable
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max(600, (int)(screen.width * 0.45));
        int h = Math.max(380, (int)(screen.height * 0.45));
        setSize(w, h);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTieuDe = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTieuDe, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        txtTenDangNhap = new JTextField(20);
        mainPanel.add(txtTenDangNhap, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        txtMatKhau = new JPasswordField(20);
        mainPanel.add(txtMatKhau, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 14));
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        mainPanel.add(btnDangNhap, gbc);

        add(mainPanel, BorderLayout.CENTER);

        // Enter key to login
        txtMatKhau.addActionListener(e -> xuLyDangNhap());
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập và mật khẩu!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan taiKhoan = taiKhoanService.xacThuc(tenDangNhap, matKhau);
        if (taiKhoan != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                FrmChinh frmChinh = new FrmChinh(taiKhoan);

                // Apply per-component scaling (option B) with factor 1.2
                UIUtils.scaleComponentTree(frmChinh, 1.2f);

                // Show main frame
                frmChinh.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            txtMatKhau.setText("");
        }
    }
}