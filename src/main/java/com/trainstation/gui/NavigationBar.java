package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
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
        // Material Professional Light - Primary Blue
        setBackground(new Color(25, 118, 210)); // #1976D2
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(0, 56)); // Fixed height for navigation bar
        
        // Left panel with navigation buttons
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
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
        
        // Station button
        JButton stationBtn = createNavButton("Ga", "station");
        leftPanel.add(stationBtn);
        
        // Train button
        JButton trainBtn = createNavButton("Đầu máy", "train");
        leftPanel.add(trainBtn);
        // Train schedule button
        JButton chuyenBtn = createNavButton("Chuyến Tàu", "traintimetable");
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

            JMenuItem deletedEmployeesItem = new JMenuItem("Dữ liệu đã xóa");
            deletedEmployeesItem.addActionListener(e -> navigateTo("deletedemployees"));
            employeeMenu.add(deletedEmployeesItem);

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
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        JLabel userLabel = new JLabel(getEmployeeName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        rightPanel.add(userLabel);
        
        add(rightPanel, BorderLayout.EAST);
    }
    
    private JButton createNavButton(String text, String action) {
        JButton button = new JButton(text);
        // Material Professional Light - White background, blue text
        button.setForeground(new Color(10, 115, 215)); // #0A73D7 - blue text
        button.setBackground(Color.WHITE); // White background
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Increase width to prevent text truncation, adjust based on text length
        int buttonWidth = Math.max(80, text.length() * 12 + 10);
        button.setPreferredSize(new Dimension(buttonWidth, 40));
        button.setMargin(new Insets(5, 15, 5, 15));
        
        // Material hover effect - white background normally, blue background on hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(10, 115, 215)); // #0A73D7 - blue hover
                button.setForeground(Color.WHITE); // White text on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE); // Back to white
                button.setForeground(new Color(10, 115, 215)); // Back to blue text
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
