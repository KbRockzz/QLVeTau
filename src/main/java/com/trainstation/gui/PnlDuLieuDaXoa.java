package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.NhanVienDAO;
import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.model.NhanVien;
import com.trainstation.model.TaiKhoan;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel hiển thị dữ liệu đã xóa cho Nhân viên và Tài khoản
 * Cho phép khôi phục những dữ liệu đã xóa.
 */
public class PnlDuLieuDaXoa extends JPanel {
    private NhanVienDAO nhanVienDAO;
    private TaiKhoanDAO taiKhoanDAO;
    
    // Employee tab components
    private JTable bangNhanVien;
    private DefaultTableModel modelNhanVien;
    private JButton btnKhoiPhucNV, btnLamMoiNV, btnXoaRongNV;
    
    // Account tab components
    private JTable bangTaiKhoan;
    private DefaultTableModel modelTaiKhoan;
    private JButton btnKhoiPhucTK, btnLamMoiTK, btnXoaRongTK;

    public PnlDuLieuDaXoa() {
        this.nhanVienDAO = NhanVienDAO.getInstance();
        this.taiKhoanDAO = TaiKhoanDAO.getInstance();
        initComponents();
        taiDuLieuNhanVienDaXoa();
        taiDuLieuTaiKhoanDaXoa();
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
     * Tải dữ liệu các nhân viên có isActive = 0 (đã xóa mềm)
     */
    private void taiDuLieuNhanVienDaXoa() {
        modelNhanVien.setRowCount(0);
        List<NhanVien> danhSach = nhanVienDAO.getAllIncludingDeleted();
        for (NhanVien nv : danhSach) {
            if (!nv.isActive()) {
                modelNhanVien.addRow(new Object[]{
                        nv.getMaNV(),
                        nv.getTenNV(),
                        nv.getSoDienThoai(),
                        nv.getDiaChi(),
                        nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "",
                        nv.getMaLoaiNV()
                });
            }
        }
    }

    /**
     * Tải dữ liệu các tài khoản có isActive = 0 (đã xóa mềm)
     */
    private void taiDuLieuTaiKhoanDaXoa() {
        modelTaiKhoan.setRowCount(0);
        List<TaiKhoan> danhSach = taiKhoanDAO.getAllIncludingDeleted();
        for (TaiKhoan tk : danhSach) {
            if (!tk.isActive()) {
                modelTaiKhoan.addRow(new Object[]{
                        tk.getMaTK(),
                        tk.getMaNV(),
                        tk.getTenTaiKhoan(),
                        tk.getTrangThai()
                });
            }
        }
    }

    /**
     * Khôi phục nhân viên đã xóa (set isActive = 1)
     */
    private void khoiPhucNhanVien() {
        int row = bangNhanVien.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần khôi phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maNV = (String) modelNhanVien.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khôi phục nhân viên " + maNV + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = nhanVienDAO.restore(maNV);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Khôi phục nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuNhanVienDaXoa();
        } else {
            JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Khôi phục tài khoản đã xóa (set isActive = 1)
     */
    private void khoiPhucTaiKhoan() {
        int row = bangTaiKhoan.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần khôi phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maTK = (String) modelTaiKhoan.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khôi phục tài khoản " + maTK + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        boolean ok = taiKhoanDAO.restore(maTK);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Khôi phục tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuTaiKhoanDaXoa();
        } else {
            JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}