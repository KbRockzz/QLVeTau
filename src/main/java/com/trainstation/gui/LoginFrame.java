package com.trainstation.gui;

import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.model.TaiKhoan;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private TaiKhoanDAO accountDAO;

    public LoginFrame() {
        accountDAO = TaiKhoanDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(400, 250);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Tên đăng nhập:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        mainPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Mật khẩu:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        mainPanel.add(passwordField, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        loginButton = new JButton("Đăng nhập");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.addActionListener(e -> handleLogin());
        mainPanel.add(loginButton, gbc);

        // Info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đăng nhập mặc định"));
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        JLabel infoLabel1 = new JLabel("Tài khoản: admin");
        JLabel infoLabel2 = new JLabel("Mật khẩu: admin123");
        infoPanel.add(infoLabel1);
        infoPanel.add(infoLabel2);

        add(mainPanel, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);

        // Enter key to login
        passwordField.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập và mật khẩu!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan account = accountDAO.authenticate(username, password);
        if (account != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(account);
                mainFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
}
