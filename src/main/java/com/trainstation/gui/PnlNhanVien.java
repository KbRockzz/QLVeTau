package com.trainstation.gui;

import com.trainstation.service.NhanVienService;
import com.trainstation.model.NhanVien;
import com.trainstation.dao.LoaiNVDAO;
import com.trainstation.model.LoaiNV;
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
    private LoaiNVDAO loaiNVDAO;
    private JTable bangNhanVien;
    private DefaultTableModel modelBang;
    private JTextField txtMaNV, txtTenNV, txtSDT, txtDiaChi, txtNgaySinh;
    private JComboBox<String> cmbLoaiNV;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnMoi;

    public PnlNhanVien() {
        this.nhanVienService = NhanVienService.getInstance();
        this.loaiNVDAO = LoaiNVDAO.getInstance();
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
        String[] tenCot = {"Mã NV", "Tên nhân viên", "Số điện thoại", "Địa chỉ", "Ngày sinh", "Loại NV"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangNhanVien = new JTable(modelBang);
        bangNhanVien.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinNhanVien();
            }
        });
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
        txtMaNV.setEditable(false);
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

        gbc.gridx = 0; gbc.gridy = 4;
        pnlForm.add(new JLabel("Ngày sinh (yyyy-mm-dd):"), gbc);
        gbc.gridx = 1;
        txtNgaySinh = new JTextField(20);
        pnlForm.add(txtNgaySinh, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        pnlForm.add(new JLabel("Loại nhân viên:"), gbc);
        gbc.gridx = 1;
        cmbLoaiNV = new JComboBox<>();
        List<LoaiNV> danhSachLoaiNV = loaiNVDAO.getAll();
        for (LoaiNV loai : danhSachLoaiNV) {
            cmbLoaiNV.addItem(loai.getMaLoai() + " - " + loai.getTenLoai());
        }
        pnlForm.add(cmbLoaiNV, gbc);

        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        
        btnMoi = new JButton("Mới");
        btnMoi.addActionListener(e -> lamMoiForm());
        pnlButton.add(btnMoi);
        
        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themNhanVien());
        pnlButton.add(btnThem);
        
        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.addActionListener(e -> capNhatNhanVien());
        pnlButton.add(btnCapNhat);
        
        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaNhanVien());
        pnlButton.add(btnXoa);
        
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
                nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "",
                nv.getMaLoaiNV()
            });
        }
    }

    private void hienThiThongTinNhanVien() {
        int row = bangNhanVien.getSelectedRow();
        if (row < 0) return;
        
        txtMaNV.setText((String) modelBang.getValueAt(row, 0));
        txtTenNV.setText((String) modelBang.getValueAt(row, 1));
        txtSDT.setText((String) modelBang.getValueAt(row, 2));
        txtDiaChi.setText((String) modelBang.getValueAt(row, 3));
        txtNgaySinh.setText((String) modelBang.getValueAt(row, 4));
        
        String maLoaiNV = (String) modelBang.getValueAt(row, 5);
        for (int i = 0; i < cmbLoaiNV.getItemCount(); i++) {
            if (cmbLoaiNV.getItemAt(i).startsWith(maLoaiNV)) {
                cmbLoaiNV.setSelectedIndex(i);
                break;
            }
        }
    }

    private void lamMoiForm() {
        txtMaNV.setText(nhanVienService.taoMaNhanVien());
        txtTenNV.setText("");
        txtSDT.setText("");
        txtDiaChi.setText("");
        txtNgaySinh.setText("");
        if (cmbLoaiNV.getItemCount() > 0) {
            cmbLoaiNV.setSelectedIndex(0);
        }
        bangNhanVien.clearSelection();
    }

    private void themNhanVien() {
        if (!kiemTraDuLieu()) return;
        
        try {
            String maNV = txtMaNV.getText().trim();
            if (maNV.isEmpty()) {
                maNV = nhanVienService.taoMaNhanVien();
            }
            
            String loaiNVStr = (String) cmbLoaiNV.getSelectedItem();
            String maLoaiNV = loaiNVStr.split(" - ")[0];
            
            LocalDate ngaySinh = null;
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim());
            }
            
            NhanVien nv = new NhanVien(
                maNV,
                txtTenNV.getText().trim(),
                txtSDT.getText().trim(),
                txtDiaChi.getText().trim(),
                ngaySinh,
                maLoaiNV
            );
            
            if (nhanVienService.themNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuNhanVien();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!kiemTraDuLieu()) return;
        
        try {
            String loaiNVStr = (String) cmbLoaiNV.getSelectedItem();
            String maLoaiNV = loaiNVStr.split(" - ")[0];
            
            LocalDate ngaySinh = null;
            if (!txtNgaySinh.getText().trim().isEmpty()) {
                ngaySinh = LocalDate.parse(txtNgaySinh.getText().trim());
            }
            
            NhanVien nv = new NhanVien(
                txtMaNV.getText().trim(),
                txtTenNV.getText().trim(),
                txtSDT.getText().trim(),
                txtDiaChi.getText().trim(),
                ngaySinh,
                maLoaiNV
            );
            
            if (nhanVienService.capNhatNhanVien(nv)) {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuNhanVien();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaNhanVien() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa nhân viên này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (nhanVienService.xoaNhanVien(txtMaNV.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuNhanVien();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa nhân viên thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean kiemTraDuLieu() {
        if (txtTenNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
