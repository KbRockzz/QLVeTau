package com.trainstation.gui;

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
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create a panel with train station background
        JPanel pnlNen = new JPanel() {
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
        pnlNen.setLayout(new BoxLayout(pnlNen, BoxLayout.Y_AXIS));
        pnlNen.setBorder(BorderFactory.createEmptyBorder(50, 20, 20, 20));
        
        // Welcome message panel
        JPanel pnlChaoMung = new JPanel();
        pnlChaoMung.setOpaque(false);
        pnlChaoMung.setLayout(new BoxLayout(pnlChaoMung, BoxLayout.Y_AXIS));
        
        JLabel lblTieuDe = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 36));
        lblTieuDe.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTieuDe.setForeground(Color.WHITE);
        
        JLabel lblChaoMung = new JLabel("Xin chào, " + layTenNhanVien());
        lblChaoMung.setFont(new Font("Arial", Font.BOLD, 24));
        lblChaoMung.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChaoMung.setForeground(Color.WHITE);
        
        JLabel lblChucVu = new JLabel("Chức vụ: " + layMoTaChucVu());
        lblChucVu.setFont(new Font("Arial", Font.PLAIN, 20));
        lblChucVu.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblChucVu.setForeground(new Color(236, 240, 241));
        
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
        JLabel lblChanTrang = new JLabel("© 2024 QLVeTau - Hệ thống quản lý vé tàu");
        lblChanTrang.setForeground(Color.GRAY);
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
