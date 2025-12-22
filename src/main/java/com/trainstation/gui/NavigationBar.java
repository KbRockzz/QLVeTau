package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.TaiKhoan;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
        setBackground(new Color(25, 118, 210));
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(0, 56));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        leftPanel.setOpaque(false);

        JButton homeBtn = createNavButton("Trang chủ", "home");
        leftPanel.add(homeBtn);

        // Chuyến tàu - Chỉ quản lý
        if (currentAccount.isManager()) {
            JButton tripManagementBtn = createNavButton("Chuyến tàu", null);
            JPopupMenu tripMenu = new JPopupMenu();
            tripMenu.add(createMenuItem("Chuyến tàu", "chuyentau"));
            tripMenu.add(createMenuItem("Đầu máy", "daumay"));
            tripMenu.add(createMenuItem("Toa tàu", "toatau"));
            tripMenu.add(createMenuItem("Ga tàu","ga"));
            tripManagementBtn.addActionListener(e -> tripMenu.show(tripManagementBtn, 0, tripManagementBtn.getHeight()));
            leftPanel.add(tripManagementBtn);
        }

        // Tìm kiếm chuyến tàu - Tất cả nhân viên
        JButton searchTripBtn = createNavButton("Tìm chuyến", "timkiemct");
        leftPanel.add(searchTripBtn);

        JButton ticketManagementBtn = createNavButton("Quản lý vé", null);
        JPopupMenu ticketManagementMenu = new JPopupMenu();
        // Bảng giá - Chỉ quản lý
        if (currentAccount.isManager()) {
            ticketManagementMenu.add(createMenuItem("Bảng giá", "banggia"));
            ticketManagementMenu.addSeparator();
        }
        ticketManagementMenu.add(createMenuItem("Đặt vé","bookticket"));
        ticketManagementMenu.add(createMenuItem("Tìm vé", "searchticket"));
        ticketManagementMenu.add(createMenuItem("Đổi vé", "changeticket"));
        ticketManagementMenu.add(createMenuItem("Hoàn vé", "refundticket"));
        ticketManagementBtn.addActionListener(e -> ticketManagementMenu.show(ticketManagementBtn, 0, ticketManagementBtn.getHeight()));
        leftPanel.add(ticketManagementBtn);

        JButton customerManagementBtn = createNavButton("Khách hàng", null);
        JPopupMenu customerMenu = new JPopupMenu();
        customerMenu.add(createMenuItem("Khách hàng", "khachhang"));
        customerMenu.addSeparator();
        customerMenu.add(createMenuItem("Tìm kiếm khách hàng", "searchcustomer"));
        customerManagementBtn.addActionListener(e -> customerMenu.show(customerManagementBtn, 0, customerManagementBtn.getHeight()));
        leftPanel.add(customerManagementBtn);

        if (currentAccount.isManager()) {
            JButton staffManagementBtn = createNavButton("Nhân viên", null);
            JPopupMenu staffMenu = new JPopupMenu();
            staffMenu.add(createMenuItem("Nhân viên", "nhanvien"));
            staffMenu.add(createMenuItem("Tài khoản", "taikhoan"));
            staffMenu.addSeparator();
            staffMenu.add(createMenuItem("Tìm kiếm", "timkiemnv-tk"));
            staffManagementBtn.addActionListener(e -> staffMenu.show(staffManagementBtn, 0, staffManagementBtn.getHeight()));
            leftPanel.add(staffManagementBtn);
        }

        JButton invoiceManagementBtn = createNavButton("Hóa đơn", null);
        JPopupMenu invoiceMenu = new JPopupMenu();
        invoiceMenu.add(createMenuItem("Hóa đơn", "hoadon"));
        invoiceMenu.addSeparator();
        invoiceMenu.add(createMenuItem("Tìm kiếm hóa đơn", "timhoadon"));
        invoiceMenu.add(createMenuItem("Thống kê", "thongke"));
        invoiceManagementBtn.addActionListener(e -> invoiceMenu.show(invoiceManagementBtn, 0, invoiceManagementBtn.getHeight()));
        leftPanel.add(invoiceManagementBtn);

        if (currentAccount.isManager()) {
            JButton deletedDataBtn = createNavButton("Dữ liệu đã xóa", "deleteddata");
            leftPanel.add(deletedDataBtn);
        }

        JButton logoutBtn = createNavButton("Đăng xuất", "logout");
        leftPanel.add(logoutBtn);

        add(leftPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        JLabel userLabel = new JLabel(getEmployeeName());
        userLabel.setForeground(Color.WHITE);
        userLabel.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        rightPanel.add(userLabel);

        add(rightPanel, BorderLayout.EAST);
    }

    private JMenuItem createMenuItem(String label, String action) {
        JMenuItem item = new JMenuItem(label);
        item.addActionListener(e -> {
            if ("logout".equals(action)) {
                handleLogout();
            } else {
                navigateTo(action);
            }
        });
        return item;
    }

    private JButton createNavButton(String text, String action) {
        JButton button = new JButton(text);
        button.setForeground(new Color(10, 115, 215));
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setFont(MaterialInitializer.createFont(Font.PLAIN, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        int buttonWidth = Math.max(80, text.length() * 12 + 10);
        button.setPreferredSize(new Dimension(buttonWidth, 40));
        button.setMargin(new Insets(5, 15, 5, 15));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(10, 115, 215));
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(new Color(10, 115, 215));
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