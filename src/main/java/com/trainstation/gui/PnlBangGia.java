/*
 * @ (#) PnlBangGia        1.0     12/15/2025
 *
 * Copyright (c) 2025 IUH. All rights reserved.
 */

package com.trainstation.gui;

import com.toedter.calendar.JDateChooser;
import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.BangGiaDAO;
import com.trainstation.dao.ChangTauDAO;
import com.trainstation.dao.LoaiGheDAO;
import com.trainstation.model.BangGia;
import com.trainstation.model.ChangTau;
import com.trainstation.model.LoaiGhe;
import com.trainstation.service.BangGiaService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Panel quản lý Bảng giá vé tàu
 */
public class PnlBangGia extends JPanel {

    // Regex pattern for validation
    private static final Pattern PATTERN_MA_BANGGIA = Pattern.compile("^BG\\w+");

    private final BangGiaService bangGiaService;
    private final BangGiaDAO bangGiaDAO = BangGiaDAO.getInstance();
    private final ChangTauDAO changTauDAO = ChangTauDAO.getInstance();
    private final LoaiGheDAO loaiGheDAO = LoaiGheDAO.getInstance();

    // Form components
    private JTextField txtMaBangGia;
    private JTextField txtGiaCoBan;
    private JComboBox<ChangTau> cboChangTau;
    private JComboBox<LoaiGhe> cboLoaiGhe;
    private JDateChooser ngayBatDau;
    private JDateChooser ngayKetThuc;

    // Table
    private DefaultTableModel modelBang;
    private JTable bang;

    // Buttons
    private JButton btnThem, btnSua, btnXoa, btnLamMoi;

    private final NumberFormat giaFormat = new DecimalFormat("#,##0.##");

    public PnlBangGia() {
        this.bangGiaService = BangGiaService.getInstance();
        initComponents();
        loadData();
        resetFormForCreate();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTitle = new JLabel("BẢNG GIÁ VÉ TÀU", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTitle, BorderLayout.NORTH);

        // ====== Form thông tin ======
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin bảng giá"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Mã bảng giá
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mã bảng giá:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMaBangGia = new JTextField(20);
        pnlInput.add(txtMaBangGia, gbc);

        // Loại chặng (ChangTau)
        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Chặng tàu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cboChangTau = new JComboBox<>();
        loadChangTauToCombo();
        pnlInput.add(cboChangTau, gbc);

        // Loại ghế
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlInput.add(new JLabel("Loại ghế:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cboLoaiGhe = new JComboBox<>();
        loadLoaiGheToCombo();
        pnlInput.add(cboLoaiGhe, gbc);

        // Giá cơ bản
        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Giá cơ bản:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtGiaCoBan = new JTextField(20);
        pnlInput.add(txtGiaCoBan, gbc);

        // Ngày bắt đầu
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Ngày bắt đầu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        ngayBatDau = new JDateChooser();
        ngayBatDau.setDateFormatString("dd/MM/yyyy");
        pnlInput.add(ngayBatDau, gbc);

        // Ngày kết thúc
        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Ngày kết thúc:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        ngayKetThuc = new JDateChooser();
        ngayKetThuc.setDateFormatString("dd/MM/yyyy");
        pnlInput.add(ngayKetThuc, gbc);

        // ====== Table ======
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
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 30);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(pnlInput, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // ====== Buttons ======
        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themBangGia());
        MaterialInitializer.styleButton(btnThem);
        pnlButton.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaBangGia());
        MaterialInitializer.styleButton(btnSua);
        pnlButton.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaBangGia());
        MaterialInitializer.styleButton(btnXoa);
        pnlButton.add(btnXoa);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            loadData();
            resetFormForCreate();
        });
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    // ====== Load combo data ======
    private void loadChangTauToCombo() {
        cboChangTau.removeAllItems();
        List<ChangTau> danhSachChangTau = changTauDAO.getAll();
        if (danhSachChangTau != null) {
            for (ChangTau ct : danhSachChangTau) {
                cboChangTau.addItem(ct);
            }
        }
    }

    private void loadLoaiGheToCombo() {
        cboLoaiGhe.removeAllItems();
        List<LoaiGhe> danhSachLoaiGhe = loaiGheDAO.getAll();
        if (danhSachLoaiGhe != null) {
            for (LoaiGhe lg : danhSachLoaiGhe) {
                cboLoaiGhe.addItem(lg);
            }
        }
    }

    // ====== CRUD handlers ======
    private void themBangGia() {
        try {
            String maBangGia = txtMaBangGia.getText().trim();
            if (maBangGia.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã bảng giá không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate maBangGia starts with BG
            if (!PATTERN_MA_BANGGIA.matcher(maBangGia).matches()) {
                JOptionPane.showMessageDialog(this, "Mã bảng giá phải bắt đầu bằng 'BG' (ví dụ: BG001, BG_2024).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (bangGiaService.timBangGiaTheoMa(maBangGia) != null) {
                JOptionPane.showMessageDialog(this, "Mã bảng giá đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BangGia bg = buildBangGiaFromForm();
            if (bg == null) return;

            if (bangGiaService.themBangGia(bg)) {
                JOptionPane.showMessageDialog(this, "Thêm bảng giá thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
                resetFormForCreate();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm bảng giá thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá cơ bản phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm bảng giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaBangGia() {
        int selectedRow = bang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bảng giá để sửa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            String maBangGia = txtMaBangGia.getText().trim();
            if (maBangGia.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Mã bảng giá không được để trống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate maBangGia starts with BG
            if (!PATTERN_MA_BANGGIA.matcher(maBangGia).matches()) {
                JOptionPane.showMessageDialog(this, "Mã bảng giá phải bắt đầu bằng 'BG' (ví dụ: BG001, BG_2024).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            BangGia bg = buildBangGiaFromForm();
            if (bg == null) return;

            if (bangGiaDAO.update(bg)) {
                JOptionPane.showMessageDialog(this, "Cập nhật bảng giá thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                loadData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật bảng giá thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Giá cơ bản phải là số hợp lệ!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi sửa bảng giá: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaBangGia() {
        int selectedRow = bang.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một bảng giá để xóa!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maBangGia = modelBang.getValueAt(selectedRow, 0).toString();
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Bạn có chắc chắn muốn xóa bảng giá " + maBangGia + " không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        if (bangGiaDAO.delete(maBangGia)) {
            JOptionPane.showMessageDialog(this, "Xóa bảng giá thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            loadData();
            resetFormForCreate();
        } else {
            JOptionPane.showMessageDialog(this, "Xóa bảng giá thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ====== Helpers ======
    private BangGia buildBangGiaFromForm() {
        String maBangGia = txtMaBangGia.getText().trim();

        ChangTau chang = (ChangTau) cboChangTau.getSelectedItem();
        if (chang == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chặng tàu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        LoaiGhe loaiGhe = (LoaiGhe) cboLoaiGhe.getSelectedItem();
        if (loaiGhe == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại ghế!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String giaStr = txtGiaCoBan.getText().trim();
        if (giaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập giá cơ bản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        float giaCoBan = Float.parseFloat(giaStr.replace(",", "").replace(".", ""));

        Date dFrom = ngayBatDau.getDate();
        Date dTo = ngayKetThuc.getDate();

        if (dFrom != null && dTo != null && dFrom.after(dTo)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        return new BangGia(
                maBangGia,
                chang.getMaChang(),
                loaiGhe.getMaLoai(),
                giaCoBan,
                dFrom != null ? new java.sql.Timestamp(dFrom.getTime()).toLocalDateTime() : null,
                dTo != null ? new java.sql.Timestamp(dTo.getTime()).toLocalDateTime() : null
        );
    }

    private void resetFormForCreate() {
        txtMaBangGia.setText("");
        txtMaBangGia.setEditable(true);
        if (cboChangTau.getItemCount() > 0) cboChangTau.setSelectedIndex(0);
        if (cboLoaiGhe.getItemCount() > 0) cboLoaiGhe.setSelectedIndex(0);
        txtGiaCoBan.setText("");
        ngayBatDau.setDate(null);
        ngayKetThuc.setDate(null);
        bang.clearSelection();
    }

    private void loadData() {
        modelBang.setRowCount(0);
        List<BangGia> danhSachBangGia = bangGiaService.layTatCaBangGia();
        if (danhSachBangGia == null) return;

        for (BangGia bg : danhSachBangGia) {
            Object[] rowData = {
                    bg.getMaBangGia(),
                    bg.getMaChang(),
                    bg.getLoaiGhe(),
                    giaFormat.format(bg.getGiaCoBan()),
                    bg.getNgayBatDau() != null ? bg.getNgayBatDau().toLocalDate().toString() : null,
                    bg.getNgayKetThuc() != null ? bg.getNgayKetThuc().toLocalDate().toString() : null
            };
            modelBang.addRow(rowData);
        }
    }

    private void hienThiThongTin() {
        int selectedRow = bang.getSelectedRow();
        if (selectedRow == -1) return;

        String maBangGia = String.valueOf(modelBang.getValueAt(selectedRow, 0));
        txtMaBangGia.setText(maBangGia);
        txtMaBangGia.setEditable(false);

        // Chọn chặng
        String maChang = String.valueOf(modelBang.getValueAt(selectedRow, 1));
        for (int i = 0; i < cboChangTau.getItemCount(); i++) {
            ChangTau ct = cboChangTau.getItemAt(i);
            if (ct != null && maChang.equals(ct.getMaChang())) {
                cboChangTau.setSelectedIndex(i);
                break;
            }
        }

        // Chọn loại ghế (so sánh theo maLoai)
        String maLoaiGhe = String.valueOf(modelBang.getValueAt(selectedRow, 2));
        for (int i = 0; i < cboLoaiGhe.getItemCount(); i++) {
            LoaiGhe lg = cboLoaiGhe.getItemAt(i);
            if (lg != null && (maLoaiGhe.equals(lg.getMaLoai()) || maLoaiGhe.equals(lg.getTenLoai()))) {
                cboLoaiGhe.setSelectedIndex(i);
                break;
            }
        }

        txtGiaCoBan.setText(String.valueOf(modelBang.getValueAt(selectedRow, 3)));

        Object ngayBDVal = modelBang.getValueAt(selectedRow, 4);
        if (ngayBDVal != null) {
            ngayBatDau.setDate(java.sql.Date.valueOf(ngayBDVal.toString()));
        } else {
            ngayBatDau.setDate(null);
        }

        Object ngayKTVal = modelBang.getValueAt(selectedRow, 5);
        if (ngayKTVal != null) {
            ngayKetThuc.setDate(java.sql.Date.valueOf(ngayKTVal.toString()));
        } else {
            ngayKetThuc.setDate(null);
        }
    }
}