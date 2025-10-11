package com.trainstation.gui;

import com.trainstation.model.Account;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

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
        
        // Create a panel with train station background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Create gradient background simulating train station
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(52, 73, 94),
                    0, getHeight(), new Color(149, 165, 166)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Draw train station silhouette
                g2d.setColor(new Color(44, 62, 80, 150));
                g2d.fillRect(50, getHeight() - 200, 150, 180);
                g2d.fillRect(220, getHeight() - 250, 200, 230);
                g2d.fillRect(440, getHeight() - 220, 180, 200);
                
                // Draw train tracks
                g2d.setColor(new Color(149, 165, 166));
                g2d.setStroke(new BasicStroke(3));
                for (int i = 0; i < getWidth(); i += 30) {
                    g2d.drawLine(i, getHeight() - 50, i + 20, getHeight() - 50);
                }
                g2d.drawLine(0, getHeight() - 45, getWidth(), getHeight() - 45);
                g2d.drawLine(0, getHeight() - 55, getWidth(), getHeight() - 55);
            }
        };
        backgroundPanel.setLayout(new BoxLayout(backgroundPanel, BoxLayout.Y_AXIS));
        backgroundPanel.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        // Welcome message panel
        JPanel welcomePanel = new JPanel();
        welcomePanel.setOpaque(false);
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(Color.WHITE);
        
        JLabel welcomeLabel = new JLabel("Xin chào, " + getEmployeeName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel("Chức vụ: " + getRoleDescription());
        roleLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roleLabel.setForeground(new Color(236, 240, 241));
        
        welcomePanel.add(Box.createVerticalStrut(100));
        welcomePanel.add(titleLabel);
        welcomePanel.add(Box.createVerticalStrut(30));
        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(15));
        welcomePanel.add(roleLabel);
        
        backgroundPanel.add(welcomePanel);
        backgroundPanel.add(Box.createVerticalGlue());
        
        add(backgroundPanel, BorderLayout.CENTER);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        JLabel footerLabel = new JLabel("© 2024 QLVeTau - Hệ thống quản lý vé tàu");
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel);
        
        add(footerPanel, BorderLayout.SOUTH);
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
