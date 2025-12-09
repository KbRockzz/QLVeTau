package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.NhanVienDAO;
import com.trainstation.model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel hiển thị dữ liệu đã xóa (trangThai = "hidden") cho Nhân viên
 * Cho phép khôi phục (set trangThai = "active") những nhân viên đã xóa.
 */
public class PnlDuLieuDaXoa extends JPanel {
    private NhanVienDAO nhanVienDAO;
    private JTable bangNhanVien;
    private DefaultTableModel modelBang;
    private JButton btnKhoiPhuc, btnLamMoi, btnXoaRong;

    public PnlDuLieuDaXoa() {
        this.nhanVienDAO = NhanVienDAO.getInstance();
        initComponents();
        taiDuLieuDaXoa();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTieuDe = new JLabel("DỮ LIỆU ĐÃ XÓA - NHÂN VIÊN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTieuDe, BorderLayout.NORTH);

        String[] tenCot = {"Mã NV", "Tên nhân viên", "Số điện thoại", "Địa chỉ", "Ngày sinh", "Loại NV"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangNhanVien = new JTable(modelBang);
        bangNhanVien.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(bangNhanVien);
        add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnKhoiPhuc = new JButton("Khôi phục");
        btnKhoiPhuc.addActionListener(e -> khoiPhucNhanVien());
        MaterialInitializer.styleButton(btnKhoiPhuc);
        pnlButton.add(btnKhoiPhuc);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDuLieuDaXoa());
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        btnXoaRong = new JButton("Bỏ chọn");
        btnXoaRong.addActionListener(e -> bangNhanVien.clearSelection());
        MaterialInitializer.styleButton(btnXoaRong);
        pnlButton.add(btnXoaRong);

        add(pnlButton, BorderLayout.SOUTH);
    }

    /**
     * Tải dữ liệu các nhân viên có trangThai = "hidden"
     */
    private void taiDuLieuDaXoa() {
        modelBang.setRowCount(0);
        List<NhanVien> danhSach = nhanVienDAO.getAll();
        for (NhanVien nv : danhSach) {
            String trangThai = nv.getTrangThai();
            if (trangThai != null && trangThai.equalsIgnoreCase("hidden")) {
                modelBang.addRow(new Object[]{
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
     * Khôi phục nhân viên đã xóa (set trangThai = "active" và gọi update)
     */
    private void khoiPhucNhanVien() {
        int row = bangNhanVien.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần khôi phục!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maNV = (String) modelBang.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khôi phục nhân viên " + maNV + "?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Lấy toàn bộ danh sách để tìm object NhanVien có đầy đủ trường trangThai (getAll trả về trangThai)
        List<NhanVien> danhSach = nhanVienDAO.getAll();
        NhanVien target = null;
        for (NhanVien nv : danhSach) {
            if (maNV.equals(nv.getMaNV())) {
                target = nv;
                break;
            }
        }

        if (target == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên " + maNV + " để khôi phục.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        target.setTrangThai("active");

        boolean ok = nhanVienDAO.update(target);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Khôi phục nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDuLieuDaXoa();
        } else {
            JOptionPane.showMessageDialog(this, "Khôi phục thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}