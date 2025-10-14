package com.trainstation.gui;

import com.trainstation.service.TaiKhoanService;
import com.trainstation.service.NhanVienService;
import com.trainstation.model.TaiKhoan;
import com.trainstation.model.NhanVien;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý tài khoản
 */
public class PnlTaiKhoan extends JPanel {
    private TaiKhoanService taiKhoanService;
    private NhanVienService nhanVienService;
    private JTable bangTaiKhoan;
    private DefaultTableModel modelBang;
    private JTextField txtMaTK, txtMaNV, txtTenTK, txtMatKhau;
    private JComboBox<String> cmbTrangThai;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnMoi, btnDoiMatKhau;

    public PnlTaiKhoan() {
        this.taiKhoanService = TaiKhoanService.getInstance();
        this.nhanVienService = NhanVienService.getInstance();
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
        String[] tenCot = {"Mã TK", "Mã NV", "Tên tài khoản", "Trạng thái", "Loại NV"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangTaiKhoan = new JTable(modelBang);
        bangTaiKhoan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinTaiKhoan();
            }
        });
        JScrollPane scrollPane = new JScrollPane(bangTaiKhoan);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã TK:"), gbc);
        gbc.gridx = 1;
        txtMaTK = new JTextField(20);
        txtMaTK.setEditable(false);
        pnlForm.add(txtMaTK, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1;
        txtMaNV = new JTextField(20);
        pnlForm.add(txtMaNV, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Tên tài khoản:"), gbc);
        gbc.gridx = 1;
        txtTenTK = new JTextField(20);
        pnlForm.add(txtTenTK, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        txtMatKhau = new JPasswordField(20);
        pnlForm.add(txtMatKhau, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        pnlForm.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        cmbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Khóa"});
        pnlForm.add(cmbTrangThai, gbc);

        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        
        btnMoi = new JButton("Mới");
        btnMoi.addActionListener(e -> lamMoiForm());
        pnlButton.add(btnMoi);
        
        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themTaiKhoan());
        pnlButton.add(btnThem);
        
        btnCapNhat = new JButton("Cập nhật");
        btnCapNhat.addActionListener(e -> capNhatTaiKhoan());
        pnlButton.add(btnCapNhat);
        
        btnDoiMatKhau = new JButton("Đổi mật khẩu");
        btnDoiMatKhau.addActionListener(e -> doiMatKhau());
        pnlButton.add(btnDoiMatKhau);
        
        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaTaiKhoan());
        pnlButton.add(btnXoa);
        
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDuLieuTaiKhoan());
        pnlButton.add(btnLamMoi);

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.add(pnlForm, BorderLayout.CENTER);
        pnlDuoi.add(pnlButton, BorderLayout.SOUTH);

        add(pnlDuoi, BorderLayout.SOUTH);
    }

    private void taiDuLieuTaiKhoan() {
        modelBang.setRowCount(0);
        List<TaiKhoan> danhSach = taiKhoanService.layTatCaTaiKhoan();
        for (TaiKhoan tk : danhSach) {
            String loaiNV = "";
            if (tk.getMaNV() != null) {
                NhanVien nv = nhanVienService.timNhanVienTheoMa(tk.getMaNV());
                if (nv != null) {
                    loaiNV = nv.getMaLoaiNV();
                }
            }
            
            modelBang.addRow(new Object[]{
                tk.getMaTK(),
                tk.getMaNV(),
                tk.getTenTaiKhoan(),
                tk.getTrangThai(),
                loaiNV
            });
        }
    }

    private void hienThiThongTinTaiKhoan() {
        int row = bangTaiKhoan.getSelectedRow();
        if (row < 0) return;
        
        txtMaTK.setText((String) modelBang.getValueAt(row, 0));
        txtMaNV.setText((String) modelBang.getValueAt(row, 1));
        txtTenTK.setText((String) modelBang.getValueAt(row, 2));
        txtMatKhau.setText(""); // Don't show password
        cmbTrangThai.setSelectedItem((String) modelBang.getValueAt(row, 3));
    }

    private void lamMoiForm() {
        txtMaTK.setText("TK" + System.currentTimeMillis());
        txtMaNV.setText("");
        txtTenTK.setText("");
        txtMatKhau.setText("");
        cmbTrangThai.setSelectedIndex(0);
        bangTaiKhoan.clearSelection();
    }

    private void themTaiKhoan() {
        if (!kiemTraDuLieu()) return;
        
        try {
            String maTK = txtMaTK.getText().trim();
            if (maTK.isEmpty()) {
                maTK = "TK" + System.currentTimeMillis();
            }
            
            TaiKhoan tk = new TaiKhoan(
                maTK,
                txtMaNV.getText().trim(),
                txtTenTK.getText().trim(),
                txtMatKhau.getText().trim(),
                (String) cmbTrangThai.getSelectedItem()
            );
            
            if (taiKhoanService.themTaiKhoan(tk)) {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTaiKhoan();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatTaiKhoan() {
        if (txtMaTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!kiemTraDuLieu()) return;
        
        try {
            TaiKhoan tk = new TaiKhoan(
                txtMaTK.getText().trim(),
                txtMaNV.getText().trim(),
                txtTenTK.getText().trim(),
                txtMatKhau.getText().trim().isEmpty() ? 
                    taiKhoanService.timTaiKhoanTheoMa(txtMaTK.getText().trim()).getMatKhau() :
                    txtMatKhau.getText().trim(),
                (String) cmbTrangThai.getSelectedItem()
            );
            
            if (taiKhoanService.capNhatTaiKhoan(tk)) {
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTaiKhoan();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void doiMatKhau() {
        if (txtMaTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần đổi mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String matKhauMoi = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:", "Đổi mật khẩu", JOptionPane.QUESTION_MESSAGE);
        
        if (matKhauMoi != null && !matKhauMoi.trim().isEmpty()) {
            if (taiKhoanService.doiMatKhau(txtMaTK.getText().trim(), matKhauMoi)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaTaiKhoan() {
        if (txtMaTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc chắn muốn xóa tài khoản này?", 
            "Xác nhận", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (taiKhoanService.xoaTaiKhoan(txtMaTK.getText().trim())) {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTaiKhoan();
                lamMoiForm();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa tài khoản thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean kiemTraDuLieu() {
        if (txtMaNV.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (txtTenTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập tên tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
}
