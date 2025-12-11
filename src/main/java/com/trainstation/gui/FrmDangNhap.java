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
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with gradient background - fills entire frame
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                // Gradient from Primary Blue to Light Blue
                GradientPaint gp = new GradientPaint(
                    0, 0, new Color(25, 118, 210), // #1976D2
                    0, getHeight(), new Color(144, 202, 249) // #90CAF9
                );
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.CENTER;

        // Login card panel with shadow effect
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BoxLayout(cardPanel, BoxLayout.Y_AXIS));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(8, 8, 8, 8), // Outer padding for shadow effect
            BorderFactory.createEmptyBorder(40, 50, 40, 50) // Inner padding
        ));
        cardPanel.setPreferredSize(new Dimension(400, 400));
        
        // System title
        JLabel lblSystem = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU");
        lblSystem.setFont(MaterialInitializer.createFont(Font.BOLD, 18));
        lblSystem.setForeground(new Color(25, 118, 210)); // #1976D2 - Primary blue
        lblSystem.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblSystem);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Title
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(MaterialInitializer.createFont(Font.BOLD, 28));
        lblTitle.setForeground(new Color(33, 33, 33)); // #212121 - text color
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblTitle);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 35)));
        
        // Username field
        JLabel lblUsername = new JLabel("Tên đăng nhập");
        lblUsername.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        lblUsername.setForeground(new Color(33, 33, 33));
        lblUsername.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblUsername);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        txtTenDangNhap = new JTextField(20);
        txtTenDangNhap.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        txtTenDangNhap.setPreferredSize(new Dimension(300, 38));
        txtTenDangNhap.setMaximumSize(new Dimension(300, 38));
        txtTenDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(txtTenDangNhap);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Password field
        JLabel lblPassword = new JLabel("Mật khẩu");
        lblPassword.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        lblPassword.setForeground(new Color(33, 33, 33));
        lblPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        cardPanel.add(lblPassword);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        
        txtMatKhau = new JPasswordField(20);
        txtMatKhau.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        txtMatKhau.setPreferredSize(new Dimension(300, 38));
        txtMatKhau.setMaximumSize(new Dimension(300, 38));
        txtMatKhau.setAlignmentX(Component.CENTER_ALIGNMENT);
        txtMatKhau.addActionListener(e -> xuLyDangNhap()); // Enter key triggers login
        cardPanel.add(txtMatKhau);
        
        cardPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Login button
        btnDangNhap = new JButton("Đăng Nhập");
        btnDangNhap.setFont(MaterialInitializer.createFont(Font.BOLD, 15));
        btnDangNhap.setPreferredSize(new Dimension(300, 42));
        btnDangNhap.setMaximumSize(new Dimension(300, 42));
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        MaterialInitializer.styleButton(btnDangNhap);
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
