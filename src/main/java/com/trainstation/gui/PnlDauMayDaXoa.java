/*
 * @ (#) PnlDauMayDaXoa        1.0     12/12/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */
 
package com.trainstation.gui;

import com.toedter.calendar.JDateChooser;
import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.DauMay;
import com.trainstation.service.DauMayService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 12/12/2025  9:39 PM
 */
public class PnlDauMayDaXoa extends JPanel {
    private DauMayService dauMayService;
    private DefaultTableModel modelBang;
    private JTable bangDauMay;
    private JButton btnKhoiPhuc;

    public PnlDauMayDaXoa() {
        this.dauMayService = DauMayService.getInstance();
        initComponents();
        taiDuLieuDauMayDaXoa();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("ĐẦU MÁY ĐÃ XÓA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Table
        String[] tenCot = {"Mã đầu máy", "Loại đầu máy", "Tên đầu máy", "Năm sản xuất", "Lần bảo trì gần nhất", "Trạng thái"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangDauMay = new JTable(modelBang);
        JScrollPane scrollPane = new JScrollPane(bangDauMay);
        // Giảm chiều cao bảng để form phía dưới hiển thị đầy đủ
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 30);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Các nút - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();
        btnKhoiPhuc = new JButton("Khôi phục");
        btnKhoiPhuc.addActionListener(e -> khoiPhucDauMay());
        MaterialInitializer.styleButton(btnKhoiPhuc);
        pnlButton.add(btnKhoiPhuc);

        add(pnlButton, BorderLayout.SOUTH);
    }
    private void khoiPhucDauMay() {
        int selectedRow = bangDauMay.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đầu máy cần khôi phục!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn khôi phục đầu máy này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        String maDauMay = (String) modelBang.getValueAt(selectedRow, 0);
        dauMayService.khoiPhucDauMay(maDauMay);
        taiDuLieuDauMayDaXoa();
    }


    private void taiDuLieuDauMayDaXoa() {
        modelBang.setRowCount(0);
        List<DauMay> danhSach = dauMayService.layDauMayDaXoa();
        for (DauMay dauMay : danhSach) {
            Object[] rowData = {
                    dauMay.getMaDauMay(),
                    dauMay.getLoaiDauMay(),
                    dauMay.getTenDauMay(),
                    dauMay.getNamSX(),
                    dauMay.getLanBaoTriGanNhat(),
                    dauMay.getTrangThai()
            };
            modelBang.addRow(rowData);
        }
    }

}

