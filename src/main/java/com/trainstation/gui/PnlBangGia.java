/*
 * @ (#) PnlBangGia        1.0     12/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.gui;

import com.toedter.calendar.JDateChooser;
import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.*;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChangTau;
import com.trainstation.model.LoaiGhe;
import com.trainstation.service.BangGiaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/*
 * @description:
 * @author: Thuy, Ly Thi
 * @version: 1.0
 * @created: 12/15/2025  9:45 PM
 */
public class PnlBangGia extends JPanel {

    private final BangGiaService bangGiaService;
    private JTextField txtMaBangGia,txtGiaCoBan;
    private JComboBox<ChangTau> cboChangTau;
    private JComboBox<LoaiGhe> cboLoaiGhe;
    private JDateChooser ngayBatDau;
    private JDateChooser ngayKetThuc;
    private ChangTauDAO changTauDAO = ChangTauDAO.getInstance();
    private LoaiGheDAO loaiGheDAO = LoaiGheDAO.getInstance();
    private DefaultTableModel modelBang;
    private JTable bang;
    private JButton btnThemGa, btnSuaGa, btnXoaGa, btnLamMoi;


    public PnlBangGia() {
        this.bangGiaService = BangGiaService.getInstance();
        initComponents();
        loadData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //Title
        JLabel lblTitle = new JLabel("Bảng Giá Vé Tàu");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblTitle, BorderLayout.NORTH);

        // Content Panel
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin bảng giá"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mã bảng giá:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMaBangGia = new JTextField(20);
        pnlInput.add(txtMaBangGia, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Loại chặng"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cboChangTau = new JComboBox<>();
        List<ChangTau> danhSachChangTau = changTauDAO.getAll();
        for (ChangTau ct : danhSachChangTau) {
            cboChangTau.addItem(ct);
        }
        pnlInput.add(cboChangTau, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlInput.add(new JLabel("Loại ghế:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cboLoaiGhe = new JComboBox<>();
        List<LoaiGhe> danhSachLoaiGhe = loaiGheDAO.getAll();
        for (LoaiGhe lg : danhSachLoaiGhe) {
            cboLoaiGhe.addItem(lg);
        }
        pnlInput.add(cboLoaiGhe, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Giá cơ bản:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtGiaCoBan = new JTextField(20);
        pnlInput.add(txtGiaCoBan, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Ngày bắt đầu"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        ngayBatDau = new JDateChooser();
        pnlInput.add(ngayBatDau, gbc);


        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        ngayKetThuc = new JDateChooser();
        pnlInput.add(ngayKetThuc, gbc);
        add(pnlInput, BorderLayout.CENTER);

        //table

        String[] cols = {"Mã bảng giá", "Mã chặng", "Loại ghế", "Giá cơ bản", "Ngày bắt đầu", "Ngày kết thúc"};
        modelBang = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bang = new JTable(modelBang);
        bang.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bang.getSelectedRow() != -1) {
                hienThiThongTin();
            }
        });
        JScrollPane scrollPane = new JScrollPane(bang);
        // Giảm chiều cao bảng để form phía dưới hiển thị đầy đủ
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 30);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(pnlInput, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Các nút - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnThemGa = new JButton("Thêm");
        btnThemGa.addActionListener(e -> themBangGia());
        MaterialInitializer.styleButton(btnThemGa);
        pnlButton.add(btnThemGa);

        btnSuaGa = new JButton("Sửa");
        btnSuaGa.addActionListener(e -> suaBangGia());
        MaterialInitializer.styleButton(btnSuaGa);
        pnlButton.add(btnSuaGa);

        btnXoaGa = new JButton("Xóa");
        btnXoaGa.addActionListener(e -> xoaBangGia());
        MaterialInitializer.styleButton(btnXoaGa);
        pnlButton.add(btnXoaGa);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            loadData();
            xoaForm();
        });
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);

    }
    private void xoaBangGia() {
    }

    private void suaBangGia() {
    }

    private void xoaForm() {
        txtMaBangGia.setText("");
        cboChangTau.setSelectedIndex(-1);
        cboLoaiGhe.setSelectedIndex(-1);
        txtGiaCoBan.setText("");
        ngayBatDau.setDate(null);
        ngayKetThuc.setDate(null);
    }

    private void themBangGia() {
        try{
            String maBangGia = txtMaBangGia.getText().trim();
            if (bangGiaService.timBangGiaTheoMa(maBangGia) != null) {
                JOptionPane.showMessageDialog(this, "Mã bảng giá đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;}

            String maChang = ((ChangTau) cboChangTau.getSelectedItem()).getMaChang();
            String loaiGhe = ((LoaiGhe) cboLoaiGhe.getSelectedItem()).getMaLoai();
            Float giaCoBan = Float.parseFloat(txtGiaCoBan.getText().trim());
            java.util.Date ngayBD = ngayBatDau.getDate();
            java.util.Date ngayKT = ngayKetThuc.getDate();
            BangGia bg = new com.trainstation.model.BangGia(maBangGia, maChang, loaiGhe, giaCoBan,
                    ngayBD != null ? new java.sql.Timestamp(ngayBD.getTime()).toLocalDateTime() : null,
                    ngayKT != null ? new java.sql.Timestamp(ngayKT.getTime()).toLocalDateTime() : null);
            if (bangGiaService.themBangGia(bg)) {
                JOptionPane.showMessageDialog(this, "Thêm bảng giá thành công!");
                loadData();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm bảng giá thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e){
        }

    }

    private void loadData(){
        modelBang.setRowCount(0);
        List<com.trainstation.model.BangGia> danhSachBangGia = bangGiaService.layTatCaBangGia();
        for (com.trainstation.model.BangGia bg : danhSachBangGia) {
            Object[] rowData = {
                    bg.getMaBangGia(),
                    bg.getMaChang(),
                    bg.getLoaiGhe(),
                    bg.getGiaCoBan(),
                    bg.getNgayBatDau() != null ? bg.getNgayBatDau().toLocalDate().toString() : null,
                    bg.getNgayKetThuc() != null ? bg.getNgayKetThuc().toLocalDate().toString() : null
            };
            modelBang.addRow(rowData);
        }
    }
    private void hienThiThongTin(){
        int selectedRow = bang.getSelectedRow();
        if (selectedRow != -1) {
            txtMaBangGia.setText(modelBang.getValueAt(selectedRow, 0).toString());
            // Set selected item for cboChangTau
            String maChang = modelBang.getValueAt(selectedRow, 1).toString();
            for (int i = 0; i < cboChangTau.getItemCount(); i++) {
                if (cboChangTau.getItemAt(i).getMaChang().equals(maChang)) {
                    cboChangTau.setSelectedIndex(i);
                    break;
                }
            }
            // Set selected item for cboLoaiGhe
            String loaiGhe = modelBang.getValueAt(selectedRow, 2).toString();
            for (int i = 0; i < cboLoaiGhe.getItemCount(); i++) {
                if (cboLoaiGhe.getItemAt(i).getTenLoai().equals(loaiGhe)) {
                    cboLoaiGhe.setSelectedIndex(i);
                    break;
                }
            }
            txtGiaCoBan.setText(modelBang.getValueAt(selectedRow, 3).toString());
            // Set ngayBatDau
            Object ngayBatDauValue = modelBang.getValueAt(selectedRow, 4);
            if (ngayBatDauValue != null) {
                ngayBatDau.setDate(java.sql.Date.valueOf(ngayBatDauValue.toString()));
            } else {
                ngayBatDau.setDate(null);
            }
            // Set ngayKetThuc
            Object ngayKetThucValue = modelBang.getValueAt(selectedRow, 5);
            if (ngayKetThucValue != null) {
                ngayKetThuc.setDate(java.sql.Date.valueOf(ngayKetThucValue.toString()));
            } else {
                ngayKetThuc.setDate(null);
            }
        }
    }

}
