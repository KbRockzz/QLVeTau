package com.trainstation.gui;

import com.trainstation.service.TauService;
import com.trainstation.model.Tau;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý tàu
 */
public class PnlTau extends JPanel {
    private TauService tauService;
    private JTable bangTau;
    private DefaultTableModel modelBang;
    private JButton btnLamMoi;

    public PnlTau() {
        this.tauService = TauService.getInstance();
        initComponents();
        taiDuLieuTau();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTieuDe = new JLabel("QUẢN LÝ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Table
        String[] tenCot = {"Mã tàu", "Tên tàu", "Số toa", "Trạng thái"};
        modelBang = new DefaultTableModel(tenCot, 0);
        bangTau = new JTable(modelBang);
        JScrollPane scrollPane = new JScrollPane(bangTau);
        add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDuLieuTau());
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDuLieuTau() {
        modelBang.setRowCount(0);
        List<Tau> danhSach = tauService.layTatCaTau();
        for (Tau tau : danhSach) {
            modelBang.addRow(new Object[]{
                tau.getMaTau(),
                tau.getTenTau(),
                tau.getSoToa(),
                tau.getTrangThai()
            });
        }
    }
}
