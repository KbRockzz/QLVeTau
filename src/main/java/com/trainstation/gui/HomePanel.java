package com.trainstation.gui;

import com.trainstation.model.Account;
import javax.swing.*;
import java.awt.*;

/**
 * Home panel displaying welcome message and quick access buttons
 */
public class HomePanel extends JPanel {
    private Account currentAccount;
    private JFrame parentFrame;
    
    public HomePanel(Account account, JFrame parentFrame) {
        this.currentAccount = account;
        this.parentFrame = parentFrame;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Welcome panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel welcomeLabel = new JLabel("Xin chào, " + getEmployeeName());
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Vai trò: " + getRoleDescription());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setForeground(Color.GRAY);
        
        welcomePanel.add(Box.createVerticalStrut(50));
        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createVerticalStrut(20));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(10));
        welcomePanel.add(roleLabel);
        
        add(welcomePanel, BorderLayout.NORTH);
        
        // Quick access panel
        JPanel quickAccessPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        quickAccessPanel.setBorder(BorderFactory.createTitledBorder("Truy cập nhanh"));
        
        // Add quick access buttons
        quickAccessPanel.add(createQuickAccessButton("🎫 Đặt vé", "Quản lý và đặt vé cho khách hàng", "ticket"));
        quickAccessPanel.add(createQuickAccessButton("👥 Khách hàng", "Quản lý thông tin khách hàng", "customer"));
        quickAccessPanel.add(createQuickAccessButton("🚆 Chuyến tàu", "Quản lý lịch trình chuyến tàu", "train"));
        
        if (currentAccount.isManager()) {
            quickAccessPanel.add(createQuickAccessButton("👤 Nhân viên", "Quản lý nhân viên", "employee"));
            quickAccessPanel.add(createQuickAccessButton("🔐 Tài khoản", "Quản lý tài khoản hệ thống", "account"));
            quickAccessPanel.add(createQuickAccessButton("📊 Thống kê", "Xem báo cáo và thống kê", "statistics"));
        }
        
        add(quickAccessPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        JLabel footerLabel = new JLabel("© 2024 QLVeTau - Hệ thống quản lý vé tàu");
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createQuickAccessButton(String title, String description, String action) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(Color.GRAY);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(descLabel, BorderLayout.CENTER);
        
        // Add click listener
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigateTo(action);
            }
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(236, 240, 241));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(null);
            }
        });
        
        return panel;
    }
    
    private void navigateTo(String page) {
        if (parentFrame instanceof MainFrame) {
            ((MainFrame) parentFrame).navigateToPage(page);
        }
    }
    
    private String getEmployeeName() {
        if (currentAccount.getEmployeeId() != null) {
            var employee = com.trainstation.dao.EmployeeDAO.getInstance()
                .findById(currentAccount.getEmployeeId());
            if (employee != null) {
                return employee.getFullName();
            }
        }
        return currentAccount.getUsername();
    }
    
    private String getRoleDescription() {
        if (currentAccount.isManager()) {
            return "Quản lý hệ thống";
        } else {
            return "Nhân viên";
        }
    }
}
