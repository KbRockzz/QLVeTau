package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.TaiKhoan;
import com.trainstation.dao.NhanVienDAO;
import com.trainstation.model.NhanVien;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * Panel trang chủ hiển thị thông tin chào mừng
 */
public class PnlTrangChu extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private JFrame khungCha;
    
    public PnlTrangChu(TaiKhoan taiKhoan, JFrame khungCha) {
        this.taiKhoanHienTai = taiKhoan;
        this.khungCha = khungCha;
        initComponents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245)); // Material background #F5F5F5
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create a panel with Material gradient background
        JPanel pnlNen = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Material Professional Light gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(25, 118, 210), // #1976D2 - Primary Blue
                    0, getHeight(), new Color(33, 150, 243) // #2196F3 - Light Blue
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pnlNen.setLayout(new BoxLayout(pnlNen, BoxLayout.Y_AXIS));
        pnlNen.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        // Welcome message panel
        JPanel pnlChaoMung = new JPanel();
        pnlChaoMung.setOpaque(false);
        pnlChaoMung.setLayout(new BoxLayout(pnlChaoMung, BoxLayout.Y_AXIS));
        
        JLabel lblTieuDe = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU");
        lblTieuDe.setFont(MaterialInitializer.createFont(Font.BOLD, 36));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTieuDe.setForeground(Color.WHITE);
        
        JLabel lblChaoMung = new JLabel("Xin chào, " + layTenNhanVien());
        lblChaoMung.setFont(MaterialInitializer.createFont(Font.BOLD, 24));
        lblChaoMung.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChaoMung.setForeground(Color.WHITE);
        
        JLabel lblChucVu = new JLabel("Chức vụ: " + layMoTaChucVu());
        lblChucVu.setFont(MaterialInitializer.createFont(Font.PLAIN, 20));
        lblChucVu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChucVu.setForeground(new Color(227, 242, 253)); // #E3F2FD - Very Light Blue
        
        pnlChaoMung.add(Box.createVerticalStrut(100));
        pnlChaoMung.add(lblTieuDe);
        pnlChaoMung.add(Box.createVerticalStrut(30));
        pnlChaoMung.add(lblChaoMung);
        pnlChaoMung.add(Box.createVerticalStrut(15));
        pnlChaoMung.add(lblChucVu);
        
        pnlNen.add(pnlChaoMung);
        pnlNen.add(Box.createVerticalGlue());
        
        add(pnlNen, BorderLayout.CENTER);
        
        // Footer panel
        JPanel pnlChanTrang = new JPanel();
        pnlChanTrang.setBackground(new Color(245, 245, 245)); // Material background
        JLabel lblChanTrang = new JLabel("© 2024 QLVeTau - Hệ thống quản lý vé tàu");
        lblChanTrang.setFont(MaterialInitializer.createFont(Font.PLAIN, 12));
        lblChanTrang.setForeground(new Color(117, 117, 117)); // #757575 - Secondary text
        pnlChanTrang.add(lblChanTrang);
        
        add(pnlChanTrang, BorderLayout.SOUTH);
    }

    
    private String layTenNhanVien() {
        if (taiKhoanHienTai.getMaNV() != null) {
            NhanVien nhanVien = NhanVienDAO.getInstance()
                .findById(taiKhoanHienTai.getMaNV());
            if (nhanVien != null) {
                return nhanVien.getTenNV();
            }
        }
        return taiKhoanHienTai.getTenTaiKhoan();
    }
    
    private String layMoTaChucVu() {
        if (taiKhoanHienTai.isManager()) {
            return "Quản lý hệ thống";
        } else {
            return "Nhân viên";
        }
    }
}
