package com.trainstation.gui;

import com.trainstation.model.TaiKhoan;
import javax.swing.*;
import java.awt.*;

/**
 * Form chính của ứng dụng
 */
public class FrmChinh extends JFrame {
    private TaiKhoan taiKhoanHienTai;
    private JPanel pnlNoiDung;
    private CardLayout cardLayout;
    private NavigationBar thanhDieuHuong;

    public FrmChinh(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Hệ thống quản lý vé tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Auto-maximize window
        setLayout(new BorderLayout());

        // Navigation bar at the top
        thanhDieuHuong = new NavigationBar(taiKhoanHienTai, this);
        add(thanhDieuHuong, BorderLayout.NORTH);

        // Content panel with CardLayout for switching between panels
        cardLayout = new CardLayout();
        pnlNoiDung = new JPanel(cardLayout);
        
        // Add all panels
        pnlNoiDung.add(new PnlTrangChu(taiKhoanHienTai, this), "home");
        pnlNoiDung.add(taoPanelVoiBo(new PnlDatVe(taiKhoanHienTai)), "bookticket");
        pnlNoiDung.add(taoPanelVoiBo(new PnlHoanVe(taiKhoanHienTai)), "refundticket");
        pnlNoiDung.add(taoPanelVoiBo(new PnlDoiVe(taiKhoanHienTai)), "changeticket");
        pnlNoiDung.add(taoPanelVoiBo(new PnlQuanLyVe(taiKhoanHienTai)), "ticketbooking");
        pnlNoiDung.add(taoPanelVoiBo(new PnlKhachHang()), "customer");
        pnlNoiDung.add(taoPanelVoiBo(new PnlTau()), "train");
        
        // Only add employee and account panels for managers
        if (taiKhoanHienTai.isManager()) {
            pnlNoiDung.add(taoPanelVoiBo(new PnlNhanVien()), "employee");
            pnlNoiDung.add(taoPanelVoiBo(new PnlTaiKhoan()), "account");
            pnlNoiDung.add(taoPanelVoiBo(new PnlThongKe()), "statistics");
        }

        add(pnlNoiDung, BorderLayout.CENTER);
        
        // Show home page by default
        cardLayout.show(pnlNoiDung, "home");
    }
    
    /**
     * Wrap a panel to ensure consistent layout
     */
    private JPanel taoPanelVoiBo(JPanel panel) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(panel, BorderLayout.CENTER);
        return wrapper;
    }

    /**
     * Điều hướng đến trang cụ thể
     * @param trang Tên định danh trang
     */
    public void dieuHuongDenTrang(String trang) {
        // Check if manager-only pages are accessed by non-managers
        if (!taiKhoanHienTai.isManager() && 
            (trang.equals("employee") || trang.equals("account") || trang.equals("statistics"))) {
            JOptionPane.showMessageDialog(this,
                "Bạn không có quyền truy cập trang này!\nChỉ quản lý (LNV03) mới có thể truy cập.",
                "Từ chối truy cập",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            cardLayout.show(pnlNoiDung, trang);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi chuyển trang: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Method cho khả năng tương thích ngược với NavigationBar cũ
     */
    public void navigateToPage(String page) {
        dieuHuongDenTrang(page);
    }
}
