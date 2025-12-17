package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.NhanVienDAO;
import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.model.TaiKhoan;
import com.trainstation.model.KhachHang;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel hiển thị dữ liệu đã xóa cho Nhân viên, Tài khoản và Khách hàng
 * Cho phép khôi phục những dữ liệu đã xóa.
 */
public class PnlDuLieuDaXoa extends JPanel {
    private NhanVienDAO nhanVienDAO;
    private TaiKhoanDAO taiKhoanDAO;
    private KhachHangDAO khachHangDAO;
    
    // Employee tab components
    private JTable bangNhanVien;
    private DefaultTableModel modelNhanVien;
    private JButton btnKhoiPhucNV, btnLamMoiNV, btnXoaRongNV;
    
    // Account tab components
    private JTable bangTaiKhoan;
    private DefaultTableModel modelTaiKhoan;
    private JButton btnKhoiPhucTK, btnLamMoiTK, btnXoaRongTK;
    
    // Customer tab components
    private JTable bangKhachHang;
    private DefaultTableModel modelKhachHang;
    private JButton btnKhoiPhucKH, btnLamMoiKH, btnXoaRongKH;

    public PnlDuLieuDaXoa() {
        this.nhanVienDAO = NhanVienDAO.getInstance();
        this.taiKhoanDAO = TaiKhoanDAO.getInstance();
        this.khachHangDAO = KhachHangDAO.getInstance();
        initComponents();
        taiDuLieuNhanVienDaXoa();
        taiDuLieuTaiKhoanDaXoa();
        taiDuLieuKhachHangDaXoa();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTieuDe = new JLabel("DỮ LIỆU ĐÃ XÓA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTieuDe, BorderLayout.NORTH);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Employee tab
        JPanel employeePanel = createEmployeePanel();
        tabbedPane.addTab("Nhân viên", employeePanel);
        
        // Account tab
        JPanel accountPanel = createAccountPanel();
        tabbedPane.addTab("Tài khoản", accountPanel);
        
        // Customer tab
        JPanel customerPanel = createCustomerPanel();
        tabbedPane.addTab("Khách hàng", customerPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Create employee restore panel
     */
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] tenCot = {"Mã NV", "Tên nhân viên", "Số điện thoại", "Địa chỉ", "Ngày sinh", "Loại NV"};
        modelNhanVien = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangNhanVien = new JTable(modelNhanVien);
        bangNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bangNhanVien);
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 45);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnKhoiPhucNV = new JButton("Khôi phục");
        btnKhoiPhucNV.addActionListener(e -> khoiPhucNhanVien());
        MaterialInitializer.styleButton(btnKhoiPhucNV);
        pnlButton.add(btnKhoiPhucNV);

        btnLamMoiNV = new JButton("Làm mới");
        btnLamMoiNV.addActionListener(e -> taiDuLieuNhanVienDaXoa());
        MaterialInitializer.styleButton(btnLamMoiNV);
        pnlButton.add(btnLamMoiNV);

        btnXoaRongNV = new JButton("Bỏ chọn");
        btnXoaRongNV.addActionListener(e -> bangNhanVien.clearSelection());
        MaterialInitializer.styleButton(btnXoaRongNV);
        pnlButton.add(btnXoaRongNV);

        panel.add(pnlButton, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Create account restore panel
     */
    private JPanel createAccountPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] tenCot = {"Mã TK", "Mã NV", "Tên tài khoản", "Trạng thái TK"};
        modelTaiKhoan = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangTaiKhoan = new JTable(modelTaiKhoan);
        bangTaiKhoan.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bangTaiKhoan);
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 45);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnKhoiPhucTK = new JButton("Khôi phục");
        btnKhoiPhucTK.addActionListener(e -> khoiPhucTaiKhoan());
        MaterialInitializer.styleButton(btnKhoiPhucTK);
        pnlButton.add(btnKhoiPhucTK);

        btnLamMoiTK = new JButton("Làm mới");
        btnLamMoiTK.addActionListener(e -> taiDuLieuTaiKhoanDaXoa());
        MaterialInitializer.styleButton(btnLamMoiTK);
        pnlButton.add(btnLamMoiTK);

        btnXoaRongTK = new JButton("Bỏ chọn");
        btnXoaRongTK.addActionListener(e -> bangTaiKhoan.clearSelection());
        MaterialInitializer.styleButton(btnXoaRongTK);
        pnlButton.add(btnXoaRongTK);

        panel.add(pnlButton, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Create customer restore panel
     */
    private JPanel createCustomerPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        String[] tenCot = {"Mã KH", "Tên khách hàng", "Email", "Số điện thoại"};
        modelKhachHang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangKhachHang = new JTable(modelKhachHang);
        bangKhachHang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bangKhachHang);
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 45);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnKhoiPhucKH = new JButton("Khôi phục");
        btnKhoiPhucKH.addActionListener(e -> khoiPhucKhachHang());
        MaterialInitializer.styleButton(btnKhoiPhucKH);
        pnlButton.add(btnKhoiPhucKH);

        btnLamMoiKH = new JButton("Làm mới");
        btnLamMoiKH.addActionListener(e -> taiDuLieuKhachHangDaXoa());
        MaterialInitializer.styleButton(btnLamMoiKH);
        pnlButton.add(btnLamMoiKH);

        btnXoaRongKH = new JButton("Bỏ chọn");
        btnXoaRongKH.addActionListener(e -> bangKhachHang.clearSelection());
        MaterialInitializer.styleButton(btnXoaRongKH);
        pnlButton.add(btnXoaRongKH);

        panel.add(pnlButton, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Tải dữ liệu các nhân viên có isActive = 0 (đã xóa mềm)
     */
    private void taiDuLieuNhanVienDaXoa() {
        modelNhanVien.setRowCount(0);
        // Soft delete removed - no longer showing deleted data
        // List<NhanVien> danhSach = nhanVienDAO.getAll();
        // for (NhanVien nv : danhSach) {
        //     modelNhanVien.addRow(new Object[]{
        //             nv.getMaNV(),
        //             nv.getTenNV(),
        //             nv.getSoDienThoai(),
        //             nv.getDiaChi(),
        //             nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "",
        //             nv.getMaLoaiNV()
        //     });
        // }
    }

    /**
     * Tải dữ liệu các tài khoản đã xóa - chức năng không khả dụng do đã loại bỏ soft delete
     */
    private void taiDuLieuTaiKhoanDaXoa() {
        modelTaiKhoan.setRowCount(0);
        // Soft delete removed - no longer showing deleted data
    }

    /**
     * Khôi phục nhân viên đã xóa - chức năng không khả dụng do đã loại bỏ soft delete
     */
    private void khoiPhucNhanVien() {
        JOptionPane.showMessageDialog(this, 
            "Chức năng khôi phục không khả dụng do đã loại bỏ soft delete.\nDữ liệu đã xóa sẽ bị xóa vĩnh viễn.", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Khôi phục tài khoản đã xóa - chức năng không khả dụng do đã loại bỏ soft delete
     */
    private void khoiPhucTaiKhoan() {
        JOptionPane.showMessageDialog(this, 
            "Chức năng khôi phục không khả dụng do đã loại bỏ soft delete.\nDữ liệu đã xóa sẽ bị xóa vĩnh viễn.", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Tải dữ liệu các khách hàng đã xóa - chức năng không khả dụng do đã loại bỏ soft delete
     */
    private void taiDuLieuKhachHangDaXoa() {
        modelKhachHang.setRowCount(0);
        // Soft delete removed - no longer showing deleted data
    }

    /**
     * Khôi phục khách hàng đã xóa - chức năng không khả dụng do đã loại bỏ soft delete
     */
    private void khoiPhucKhachHang() {
        JOptionPane.showMessageDialog(this, 
            "Chức năng khôi phục không khả dụng do đã loại bỏ soft delete.\nDữ liệu đã xóa sẽ bị xóa vĩnh viễn.", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}