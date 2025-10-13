package com.trainstation.gui;

import com.trainstation.model.TaiKhoan;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private TaiKhoan currentAccount;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private NavigationBar navigationBar;

    public MainFrame(TaiKhoan account) {
        this.currentAccount = account;
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Hệ thống quản lý vé tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Navigation bar at the top
        navigationBar = new NavigationBar(currentAccount, this);
        add(navigationBar, BorderLayout.NORTH);

        // Content panel with CardLayout for switching between panels
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        // Add all panels
        contentPanel.add(new HomePanel(currentAccount, this), "home");
        contentPanel.add(createPanelWithNav(new BookTicketPanel(currentAccount)), "bookticket");
        contentPanel.add(createPanelWithNav(new RefundTicketPanel(currentAccount)), "refundticket");
        contentPanel.add(createPanelWithNav(new ChangeTicketPanel(currentAccount)), "changeticket");
        contentPanel.add(createPanelWithNav(new CustomerPanel()), "customer");
        contentPanel.add(createPanelWithNav(new TrainPanel()), "train");
        
        // Only add employee and account panels for managers
        if (currentAccount.isManager()) {
            contentPanel.add(createPanelWithNav(new EmployeePanel()), "employee");
            contentPanel.add(createPanelWithNav(new AccountPanel()), "account");
            contentPanel.add(createPanelWithNav(new StatisticsPanel()), "statistics");
        }

        add(contentPanel, BorderLayout.CENTER);
        
        // Show home page by default
        cardLayout.show(contentPanel, "home");
    }
    
    /**
     * Wrap a panel to ensure consistent layout
     */
    private JPanel createPanelWithNav(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Navigate to a specific page
     * @param page The page identifier
     */
    public void navigateToPage(String page) {
        // Check if manager-only pages are accessed by non-managers
        if (!currentAccount.isManager() && 
            (page.equals("employee") || page.equals("account") || page.equals("statistics"))) {
            JOptionPane.showMessageDialog(this,
                "Bạn không có quyền truy cập trang này!\nChỉ quản lý (LNV03) mới có thể truy cập.",
                "Từ chối truy cập",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            cardLayout.show(contentPanel, page);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi chuyển trang: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
