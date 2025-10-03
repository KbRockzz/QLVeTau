package com.trainstation.gui;

import com.trainstation.model.Account;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Account currentAccount;
    private JTabbedPane tabbedPane;

    public MainFrame(Account account) {
        this.currentAccount = account;
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Hệ thống quản lý vé tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Hệ thống");
        JMenuItem logoutItem = new JMenuItem("Đăng xuất");
        logoutItem.addActionListener(e -> handleLogout());
        JMenuItem exitItem = new JMenuItem("Thoát");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(logoutItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Xin chào, " + currentAccount.getUsername() + " (" + currentAccount.getRole() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Add tabs based on role
        tabbedPane.addTab("🎫 Đặt vé", new TicketBookingPanel(currentAccount));
        tabbedPane.addTab("👥 Khách hàng", new CustomerPanel());
        tabbedPane.addTab("🚆 Chuyến tàu", new TrainPanel());
        
        if ("ADMIN".equals(currentAccount.getRole())) {
            tabbedPane.addTab("👤 Nhân viên", new EmployeePanel());
            tabbedPane.addTab("🔐 Tài khoản", new AccountPanel());
            tabbedPane.addTab("📊 Thống kê", new StatisticsPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc muốn đăng xuất?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            });
        }
    }
}
