package com.trainstation.gui;

import com.trainstation.model.TaiKhoan;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Form chính của ứng dụng — cập nhật để tương thích với cấu trúc NavigationBar mới.
 *
 * Ghi chú:
 * - Các trang (card) được đăng ký với các khóa khớp với id action mà NavigationBar gửi:
 *   ví dụ: "chuyentau", "chitietchuyen", "daumay", "toatau", "changtau",
 *            "ve", "loaive", "loaighe", "banggia",
 *            "khachhang", "lichsuve",
 *            "nhanvien", "loainhanvien", "taikhoan",
 *            "hoadon", "chitiethoadon",
 *            "deleted_chuyentau", "deleted_ve", "deleted_khachhang",
 *            "deleted_nhanvien", "deleted_taikhoan", "deleted_hoadon"
 *
 * - Nếu một panel chuyên biệt chưa tồn tại trong codebase, FrmChinh sẽ tạo một placeholder panel
 *   hiển thị thông báo "Chưa có giao diện" thay cho panel thật để tránh lỗi ClassNotFound.
 *
 * - Quyền truy cập: các trang dành cho quản lý vẫn chỉ được thêm / hiển thị nếu tài khoản hiện tại là manager.
 */
public class FrmChinh extends JFrame {
    private TaiKhoan taiKhoanHienTai;
    private JPanel pnlNoiDung;
    private CardLayout cardLayout;
    private NavigationBar thanhDieuHuong;
    private final Set<String> registeredPages = new HashSet<>();

    public FrmChinh(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Hệ thống quản lý vé tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prefer maximizing the window so all child panels use available space
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Also set size to screen bounds to be safe
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // Navigation bar at the top
        thanhDieuHuong = new NavigationBar(taiKhoanHienTai, this);
        add(thanhDieuHuong, BorderLayout.NORTH);

        // Content panel with CardLayout for switching between panels
        cardLayout = new CardLayout();
        pnlNoiDung = new JPanel(cardLayout);

        // Register pages (map menu ids to panels). Reuse existing panels when available,
        // otherwise use placeholders so navigation won't fail.
        // Home
        addPage("home", new PnlTrangChu(taiKhoanHienTai, this));

        // Ticket related (reuse existing PnlDatVe/PnlHoanVe/PnlDoiVe/PnlQuanLyVe)
        addPage("bookticket", taoPanelVoiBo(new PnlDatVe(taiKhoanHienTai)));
        addPage("refundticket", taoPanelVoiBo(new PnlHoanVe(taiKhoanHienTai)));
        addPage("changeticket", taoPanelVoiBo(new PnlDoiVe(taiKhoanHienTai)));
        addPage("ticketbooking", taoPanelVoiBo(new PnlQuanLyVe(taiKhoanHienTai))); // legacy id

        // Customer
        addPage("khachhang", taoPanelVoiBo(new PnlKhachHang()));
        addPage("customer", taoPanelVoiBo(new PnlKhachHang())); // legacy id

        // Stations / trains
        addPage("daumay", taoPanelVoiBo(new PnlDauMay()));      // Đầu máy
        addPage("train", taoPanelVoiBo(new PnlDauMay()));       // legacy id
        addPage("station", taoPanelVoiBo(new PnlGa()));         // Ga
        addPage("traintimetable", taoPanelVoiBo(new PnlChuyenTau()));
        addPage("chuyentau", taoPanelVoiBo(new PnlChuyenTau())); // "Chuyến tàu" in new nav
        addPage("chitietchuyen", taoPanelVoiBo(createPlaceholderPanel("Chi tiết chuyến"))); // placeholder

        // Ticket-related auxiliary pages

        addPage("banggia", taoPanelVoiBo(createPlaceholderPanel("Bảng giá"))); // placeholder

        // Invoice
        addPage("hoadon", taoPanelVoiBo(new PnlQuanLyVe(taiKhoanHienTai))); // Reuse ticket management panel as placeholder
        addPage("chitiethoadon", taoPanelVoiBo(createPlaceholderPanel("Chi tiết hóa đơn"))); // placeholder

        // Employee / account pages (manager only)
        if (taiKhoanHienTai.isManager()) {
            addPage("nhanvien", taoPanelVoiBo(new PnlNhanVien()));
            addPage("employee", taoPanelVoiBo(new PnlNhanVien())); // legacy id
            addPage("loainhanvien", taoPanelVoiBo(createPlaceholderPanel("Loại nhân viên"))); // placeholder
            addPage("taikhoan", taoPanelVoiBo(new PnlTaiKhoan()));
            addPage("account", taoPanelVoiBo(new PnlTaiKhoan())); // legacy id
            addPage("statistics", taoPanelVoiBo(new PnlThongKe()));

            // Deleted-data management (manager only). Reuse generic deleted-data panel if specific ones don't exist.
            addPage("deleted_chuyentau", taoPanelVoiBo(new PnlDuLieuDaXoa()));
            addPage("deleted_ve", taoPanelVoiBo(new PnlDuLieuDaXoa()));
            addPage("deleted_khachhang", taoPanelVoiBo(new PnlDuLieuDaXoa()));
            addPage("deleted_nhanvien", taoPanelVoiBo(new PnlDuLieuDaXoa()));
            addPage("deleted_taikhoan", taoPanelVoiBo(new PnlDuLieuDaXoa()));
            addPage("deleted_hoadon", taoPanelVoiBo(new PnlDuLieuDaXoa()));

            // Backwards-compatible keys
            addPage("deletedtrains", taoPanelVoiBo(new PnlDauMayDaXoa()));
            addPage("deletedemployees", taoPanelVoiBo(new PnlDuLieuDaXoa()));
        } else {
            // For non-managers, ensure deleted pages are not registered
        }

        // Add the content panel to frame
        add(pnlNoiDung, BorderLayout.CENTER);

        // Show home page by default
        showPage("home");

        // If this frame is created directly (not scaled by caller), apply scaling once
        if (getRootPane() != null && getRootPane().getClientProperty("ui.scaled") == null) {
            UIUtils.scaleComponentTree(this, 1.2f);
            getRootPane().putClientProperty("ui.scaled", Boolean.TRUE);
        }

        // Force update UI tree to ensure any runtime LAF/font changes are applied
        SwingUtilities.invokeLater(() -> {
            SwingUtilities.updateComponentTreeUI(this);
            revalidate();
            repaint();
        });
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
     * Add a page to the card layout if not already added.
     * @param id key used for navigation
     * @param panel panel (already wrapped) to register
     */
    private void addPage(String id, JPanel panel) {
        if (id == null || id.isBlank() || registeredPages.contains(id)) return;
        pnlNoiDung.add(panel, id);
        registeredPages.add(id);
    }

    /**
     * Hiển thị trang với khóa nhất định.
     * @param trang Tên định danh trang
     */
    public void dieuHuongDenTrang(String trang) {
        // Validate manager-only access for sensitive pages
        if (!taiKhoanHienTai.isManager() &&
                (trang.equals("nhanvien") || trang.equals("employee") || trang.equals("taikhoan")
                        || trang.equals("account") || trang.equals("statistics")
                        || trang.startsWith("deleted_") )
        ) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không có quyền truy cập trang này!\nChỉ quản lý mới có thể truy cập.",
                    "Từ chối truy cập",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!registeredPages.contains(trang)) {
            // If page not registered, show placeholder message
            JPanel placeholder = createPlaceholderPanel("Trang '" + trang + "' chưa được triển khai.");
            pnlNoiDung.add(placeholder, trang);
            registeredPages.add(trang);
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
     * Public compatibility method used by NavigationBar.
     */
    public void navigateToPage(String page) {
        dieuHuongDenTrang(page);
    }

    /**
     * Shortcut to show a page (without permission checks) — used at startup.
     */
    private void showPage(String page) {
        if (registeredPages.contains(page)) {
            cardLayout.show(pnlNoiDung, page);
        } else {
            cardLayout.show(pnlNoiDung, "home");
        }
    }

    /**
     * Create a simple placeholder panel with centered label.
     */
    private JPanel createPlaceholderPanel(String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 18));
        p.add(lbl, BorderLayout.CENTER);
        return p;
    }
}