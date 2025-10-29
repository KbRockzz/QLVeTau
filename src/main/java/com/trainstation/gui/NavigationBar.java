package com.trainstation.gui;

import com.trainstation.model.TaiKhoan;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Navigation bar component that will be displayed at the top of all panels
 */
public class NavigationBar extends JPanel {
    private TaiKhoan currentAccount;
    private JFrame parentFrame;
    
    public NavigationBar(TaiKhoan account, JFrame parentFrame) {
        this.currentAccount = account;
        this.parentFrame = parentFrame;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(52, 73, 94));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Left panel with navigation buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.setOpaque(false);
        
        // Home button
        JButton homeBtn = createNavButton("Trang chủ", "home");
        leftPanel.add(homeBtn);
        
        // Ticket management dropdown
        JButton ticketMenuBtn = createNavButton("Vé", null);
        JPopupMenu ticketMenu = new JPopupMenu();
        
        JMenuItem bookTicketItem = new JMenuItem("Đặt vé");
        bookTicketItem.addActionListener(e -> navigateTo("bookticket"));
        ticketMenu.add(bookTicketItem);
        
        JMenuItem refundTicketItem = new JMenuItem("Hoàn vé");
        refundTicketItem.addActionListener(e -> navigateTo("refundticket"));
        ticketMenu.add(refundTicketItem);
        
        JMenuItem changeTicketItem = new JMenuItem("Đổi vé");
        changeTicketItem.addActionListener(e -> navigateTo("changeticket"));
        //ticketMenu.add(changeTicketItem);

        JMenuItem invoicesManage = new JMenuItem("Xuất hóa đơn");
        invoicesManage.addActionListener(e -> navigateTo("ticketbooking"));
        ticketMenu.add(invoicesManage);
        
        ticketMenuBtn.addActionListener(e -> 
            ticketMenu.show(ticketMenuBtn, 0, ticketMenuBtn.getHeight()));
        
        leftPanel.add(ticketMenuBtn);
        
        // Customer button
        JButton customerBtn = createNavButton("Khách hàng", "customer");
        leftPanel.add(customerBtn);
        
        // Train button
        JButton trainBtn = createNavButton("Tàu", "train");
        leftPanel.add(trainBtn);
        // Train schedule button
        JButton chuyenBtn = createNavButton("Chuyến", "traintimetable");
        leftPanel.add(chuyenBtn);

        // Employee dropdown (if manager)
        if (currentAccount.isManager()) {
            JButton employeeMenuBtn = createNavButton("Nhân viên", null);
            JPopupMenu employeeMenu = new JPopupMenu();
            
            JMenuItem manageEmployeeItem = new JMenuItem("Quản lý nhân viên");
            manageEmployeeItem.addActionListener(e -> navigateTo("employee"));
            employeeMenu.add(manageEmployeeItem);
            
            JMenuItem statisticsItem = new JMenuItem("Thống kê");
            statisticsItem.addActionListener(e -> navigateTo("statistics"));
            employeeMenu.add(statisticsItem);
            
            employeeMenuBtn.addActionListener(e -> 
                employeeMenu.show(employeeMenuBtn, 0, employeeMenuBtn.getHeight()));
            
            leftPanel.add(employeeMenuBtn);
        }
        
        // Account button (if manager)
        if (currentAccount.isManager()) {
            JButton accountBtn = createNavButton("Tài khoản", "account");
            leftPanel.add(accountBtn);
        }
        
        // Logout button
        JButton logoutBtn = createNavButton("Đăng xuất", "logout");
        leftPanel.add(logoutBtn);
        
        add(leftPanel, BorderLayout.WEST);
        
        // Right panel with employee name
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        JLabel userLabel = new JLabel(getEmployeeName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rightPanel.add(userLabel);
        
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JButton createNavButton(String text, String action) {
        JButton button = new JButton(text);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(41, 128, 185));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 35));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(41, 128, 185));
            }
        });
        
        if (action != null) {
            button.addActionListener(e -> {
                if ("logout".equals(action)) {
                    handleLogout();
                } else {
                    navigateTo(action);
                }
            });
        }
        
        return button;
    }
    
    private void navigateTo(String page) {
        if (parentFrame instanceof FrmChinh) {
            ((FrmChinh) parentFrame).navigateToPage(page);
        }
    }
    
    private void handleLogout() {
        int result = JOptionPane.showConfirmDialog(
            parentFrame,
            "Bạn có chắc muốn đăng xuất?",
            "Xác nhận đăng xuất",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            parentFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                FrmDangNhap frmDangNhap = new FrmDangNhap();
                frmDangNhap.setVisible(true);
            });
        }
    }
    
    private String getEmployeeName() {
        // Get employee name from NhanVienDAO using employeeId
        if (currentAccount.getMaNV() != null) {
            var employee = com.trainstation.dao.NhanVienDAO.getInstance()
                .findById(currentAccount.getMaNV());
            if (employee != null) {
                return employee.getTenNV();
            }
        }
        return currentAccount.getTenTaiKhoan();
    }
}
