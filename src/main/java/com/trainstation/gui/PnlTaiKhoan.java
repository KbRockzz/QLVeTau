package com.trainstation.gui;

import com.trainstation.service.TaiKhoanService;
import com.trainstation.model.TaiKhoan;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý tài khoản
 */
public class PnlTaiKhoan extends JPanel {
    private TaiKhoanService taiKhoanService;
    private JTable bangTaiKhoan;
    private DefaultTableModel modelBang;
    private JButton btnLamMoi;

    public PnlTaiKhoan() {
        this.taiKhoanService = TaiKhoanService.getInstance();
        initComponents();
        taiDuLieuTaiKhoan();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTieuDe = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Table
        String[] tenCot = {"Mã TK", "Mã NV", "Tên tài khoản", "Trạng thái"};
        modelBang = new DefaultTableModel(tenCot, 0);
        bangTaiKhoan = new JTable(modelBang);
        JScrollPane scrollPane = new JScrollPane(bangTaiKhoan);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDuLieuTaiKhoan());
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDuLieuTaiKhoan() {
        modelBang.setRowCount(0);
        List<TaiKhoan> danhSach = taiKhoanService.layTatCaTaiKhoan();
        for (TaiKhoan tk : danhSach) {
            modelBang.addRow(new Object[]{
                tk.getMaTK(),
                tk.getMaNV(),
                tk.getTenTaiKhoan(),
                tk.getTrangThai()
            });
        }
    }
}
