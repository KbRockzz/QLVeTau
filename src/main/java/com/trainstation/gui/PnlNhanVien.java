package com.trainstation.gui;

import com.trainstation.service.NhanVienService;
import com.trainstation.model.NhanVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Panel quản lý nhân viên
 */
public class PnlNhanVien extends JPanel {
    private NhanVienService nhanVienService;
    private JTable bangNhanVien;
    private DefaultTableModel modelBang;
    private JTextField txtMaNV, txtTenNV, txtSDT, txtDiaChi;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi;

    public PnlNhanVien() {
        this.nhanVienService = NhanVienService.getInstance();
        initComponents();
        taiDuLieuNhanVien();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTieuDe = new JLabel("QUẢN LÝ NHÂN VIÊN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Table
        String[] tenCot = {"Mã NV", "Tên nhân viên", "Số điện thoại", "Địa chỉ", "Ngày sinh"};
        modelBang = new DefaultTableModel(tenCot, 0);
        bangNhanVien = new JTable(modelBang);
        JScrollPane scrollPane = new JScrollPane(bangNhanVien);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1;
        txtMaNV = new JTextField(20);
        pnlForm.add(txtMaNV, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Tên NV:"), gbc);
        gbc.gridx = 1;
        txtTenNV = new JTextField(20);
        pnlForm.add(txtTenNV, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        txtSDT = new JTextField(20);
        pnlForm.add(txtSDT, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        txtDiaChi = new JTextField(20);
        pnlForm.add(txtDiaChi, gbc);

        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDuLieuNhanVien());
        pnlButton.add(btnLamMoi);

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.add(pnlForm, BorderLayout.CENTER);
        pnlDuoi.add(pnlButton, BorderLayout.SOUTH);

        add(pnlDuoi, BorderLayout.SOUTH);
    }

    private void taiDuLieuNhanVien() {
        modelBang.setRowCount(0);
        List<NhanVien> danhSach = nhanVienService.layTatCaNhanVien();
        for (NhanVien nv : danhSach) {
            modelBang.addRow(new Object[]{
                nv.getMaNV(),
                nv.getTenNV(),
                nv.getSoDienThoai(),
                nv.getDiaChi(),
                nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : ""
            });
        }
    }
}
