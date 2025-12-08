package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.service.TaiKhoanService;
import com.trainstation.model.TaiKhoan;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Form đăng nhập với Material Professional Light design
 * Sử dụng FlatLaf theme để có giao diện hiện đại, phẳng
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
        setTitle("Đăng Nhập - Hệ Thống Quản Lý Vé Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with Material background color
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBackground(new Color(245, 245, 245)); // #F5F5F5 - Material background
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.CENTER;

        // Login card panel
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1), // #BDBDBD - border
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        
        // Title
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(MaterialInitializer.createFont(Font.BOLD, 24));
        lblTitle.setForeground(new Color(33, 33, 33)); // #212121 - text color
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblTitle);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Username field
        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        lblUsername.setForeground(new Color(33, 33, 33));
        lblUsername.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(lblUsername);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtTenDangNhap = new JTextField(20);
        txtTenDangNhap.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        txtTenDangNhap.setPreferredSize(new Dimension(280, 36));
        txtTenDangNhap.setMaximumSize(new Dimension(280, 36));
        txtTenDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(txtTenDangNhap);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // Password field
        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        lblPassword.setForeground(new Color(33, 33, 33));
        lblPassword.setAlignmentX(Component.LEFT_ALIGNMENT);
        cardPanel.add(lblPassword);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        txtMatKhau.setPreferredSize(new Dimension(280, 36));
        txtMatKhau.setMaximumSize(new Dimension(280, 36));
        txtMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtMatKhau.addActionListener(e -> xuLyDangNhap()); // Enter key triggers login
        cardPanel.add(txtMatKhau);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Login button
        btnDangNhap = new JButton("Đăng Nhập");
        btnDangNhap.setFont(MaterialInitializer.createFont(Font.BOLD, 14));
        btnDangNhap.setPreferredSize(new Dimension(280, 40));
        btnDangNhap.setMaximumSize(new Dimension(280, 40));
        btnDangNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // FlatLaf will apply Material colors automatically
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        cardPanel.add(btnDangNhap);
        
        mainPanel.add(cardPanel, gbc);
        
        setContentPane(mainPanel);
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
