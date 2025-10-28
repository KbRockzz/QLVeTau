package com.trainstation.gui;

import com.trainstation.service.TaiKhoanService;
import com.trainstation.service.NhanVienService;
import com.trainstation.model.TaiKhoan;
import com.trainstation.model.NhanVien;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PnlTaiKhoan extends JPanel {

    private TaiKhoanService taiKhoanService;
    private NhanVienService nhanVienService;
    private JTable bangTaiKhoan;
    private DefaultTableModel modelBang;
    private JTextField txtMaTK, txtMaNV, txtTenTK;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cmbTrangThai;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnXoaRong, btnDoiMatKhau;

    public PnlTaiKhoan() {
        this.taiKhoanService = TaiKhoanService.getInstance();
        this.nhanVienService = NhanVienService.getInstance();
        initComponents();
        taiDuLieuTaiKhoan();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTieuDe = new JLabel("QUẢN LÝ TÀI KHOẢN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        String[] tenCot = {"Mã TK", "Mã NV", "Tên tài khoản", "Trạng thái", "Loại NV"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        bangTaiKhoan = new JTable(modelBang);
        bangTaiKhoan.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) hienThiThongTinTaiKhoan();
        });
        add(new JScrollPane(bangTaiKhoan), BorderLayout.CENTER);

        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; pnlForm.add(new JLabel("Mã TK:"), gbc);
        gbc.gridx = 1; txtMaTK = new JTextField(20); txtMaTK.setEditable(false); pnlForm.add(txtMaTK, gbc);

        gbc.gridx = 0; gbc.gridy = 1; pnlForm.add(new JLabel("Mã NV:"), gbc);
        gbc.gridx = 1; txtMaNV = new JTextField(20); pnlForm.add(txtMaNV, gbc);

        gbc.gridx = 0; gbc.gridy = 2; pnlForm.add(new JLabel("Tên tài khoản:"), gbc);
        gbc.gridx = 1; txtTenTK = new JTextField(20); pnlForm.add(txtTenTK, gbc);

        gbc.gridx = 0; gbc.gridy = 3; pnlForm.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1; txtMatKhau = new JPasswordField(20); pnlForm.add(txtMatKhau, gbc);

        gbc.gridx = 0; gbc.gridy = 4; pnlForm.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1; cmbTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Khóa"}); pnlForm.add(cmbTrangThai, gbc);

        JPanel pnlButton = new JPanel(new FlowLayout());
        btnXoaRong = new JButton("Xóa rỗng"); btnXoaRong.addActionListener(e -> xoaRongForm()); pnlButton.add(btnXoaRong);
        btnThem = new JButton("Thêm"); btnThem.addActionListener(e -> themTaiKhoan()); pnlButton.add(btnThem);
        btnCapNhat = new JButton("Cập nhật"); btnCapNhat.addActionListener(e -> capNhatTaiKhoan()); pnlButton.add(btnCapNhat);
        btnDoiMatKhau = new JButton("Đổi mật khẩu"); btnDoiMatKhau.addActionListener(e -> doiMatKhau()); pnlButton.add(btnDoiMatKhau);
        btnXoa = new JButton("Xóa"); btnXoa.addActionListener(e -> xoaTaiKhoan()); pnlButton.add(btnXoa);
        btnLamMoi = new JButton("Làm mới"); btnLamMoi.addActionListener(e -> taiDuLieuTaiKhoan()); pnlButton.add(btnLamMoi);

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.add(pnlForm, BorderLayout.CENTER);
        pnlDuoi.add(pnlButton, BorderLayout.SOUTH);
        add(pnlDuoi, BorderLayout.SOUTH);
    }

    private boolean kiemTraMatKhau(String mk) {
        return mk.matches("^(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*]).{8,}$");
    }

    private boolean kiemTraDuLieu() {
        String maNV = txtMaNV.getText().trim();
        String tenTK = txtTenTK.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword()).trim();

        if (maNV.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã NV không được để trống!");
            return false;
        }
        if(!maNV.matches("^[A-Z]{2}[0-9]{2,}$")){
            showError("Mã NV không hợp lệ. Mã đúng dạng: 2 chữ hoa theo sau là số, ví dụ PL01");
            return false;
        }
        if (!tenTK.matches("^[A-Za-z0-9]{5,20}$")) {
            JOptionPane.showMessageDialog(this, "Tên tài khoản phải 5-20 ký tự (a-z, A-Z, 0-9)!");
            return false;
        }
        if (!matKhau.isEmpty() && !kiemTraMatKhau(matKhau)) {
            JOptionPane.showMessageDialog(this,
                    "Mật khẩu phải >= 8 ký tự, có ít nhất 1 chữ hoa, 1 số và 1 ký tự đặc biệt!");
            return false;
        }
        return true;
    }

    private void taiDuLieuTaiKhoan() {
        modelBang.setRowCount(0);
        List<TaiKhoan> ds = taiKhoanService.layTatCaTaiKhoan();
        for (TaiKhoan tk : ds) {
            NhanVien nv = nhanVienService.timNhanVienTheoMa(tk.getMaNV());
            String loaiNV = nv != null ? nv.getMaLoaiNV() : "";
            modelBang.addRow(new Object[]{tk.getMaTK(), tk.getMaNV(), tk.getTenTaiKhoan(), tk.getTrangThai(), loaiNV});
        }
    }

    private void hienThiThongTinTaiKhoan() {
        int r = bangTaiKhoan.getSelectedRow();
        if (r < 0) return;
        txtMaTK.setText((String) modelBang.getValueAt(r, 0));
        txtMaNV.setText((String) modelBang.getValueAt(r, 1));
        txtTenTK.setText((String) modelBang.getValueAt(r, 2));
        txtMatKhau.setText("");
        cmbTrangThai.setSelectedItem(modelBang.getValueAt(r, 3));
    }

    private void xoaRongForm() {
        txtMaTK.setText(taiKhoanService.taoMaTaiKhoan());
        txtMaNV.setText("");
        txtTenTK.setText("");
        txtMatKhau.setText("");
        cmbTrangThai.setSelectedIndex(0);
        bangTaiKhoan.clearSelection();
    }

    private void themTaiKhoan() {
        if (!kiemTraDuLieu()) return;
        String maTK = txtMaTK.getText().trim();
        if (maTK.isEmpty()) maTK = taiKhoanService.taoMaTaiKhoan();
        TaiKhoan tk = new TaiKhoan(
                maTK,
                txtMaNV.getText().trim(),
                txtTenTK.getText().trim(),
                new String(txtMatKhau.getPassword()).trim(),
                (String) cmbTrangThai.getSelectedItem()
        );
        if (taiKhoanService.themTaiKhoan(tk)) {
            JOptionPane.showMessageDialog(this, "✔ Thêm thành công!");
            taiDuLieuTaiKhoan(); xoaRongForm();
        } else JOptionPane.showMessageDialog(this, "❌ Thêm thất bại!");
    }

    private void capNhatTaiKhoan() {
        if (txtMaTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn tài khoản cần cập nhật!");
            return;
        }
        if (!kiemTraDuLieu()) return;

        String mk = new String(txtMatKhau.getPassword()).trim();
        if (mk.isEmpty())
            mk = taiKhoanService.timTaiKhoanTheoMa(txtMaTK.getText().trim()).getMatKhau();

        TaiKhoan tk = new TaiKhoan(
                txtMaTK.getText().trim(),
                txtMaNV.getText().trim(),
                txtTenTK.getText().trim(),
                mk,
                (String) cmbTrangThai.getSelectedItem()
        );
        if (taiKhoanService.capNhatTaiKhoan(tk)) {
            JOptionPane.showMessageDialog(this, "✔ Cập nhật thành công!");
            taiDuLieuTaiKhoan();
        } else JOptionPane.showMessageDialog(this, "❌ Cập nhật thất bại!");
    }

    private void doiMatKhau() {
        if (txtMaTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn tài khoản để đổi mật khẩu!");
            return;
        }
        String mk = JOptionPane.showInputDialog(this, "Nhập mật khẩu mới:");
        if (mk == null || mk.trim().isEmpty()) return;
        if (!kiemTraMatKhau(mk)) {
            JOptionPane.showMessageDialog(this, "Mật khẩu không hợp lệ!");
            return;
        }
        if (taiKhoanService.doiMatKhau(txtMaTK.getText().trim(), mk)) {
            JOptionPane.showMessageDialog(this, "✔ Đổi mật khẩu thành công!");
        } else JOptionPane.showMessageDialog(this, "❌ Đổi mật khẩu thất bại!");
    }

    private void xoaTaiKhoan() {
        if (txtMaTK.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Chọn tài khoản để xóa!");
            return;
        }
        int c = JOptionPane.showConfirmDialog(this, "Xóa tài khoản này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (c == JOptionPane.YES_OPTION) {
            if (taiKhoanService.xoaTaiKhoan(txtMaTK.getText().trim())) {
                JOptionPane.showMessageDialog(this, "✔ Xóa thành công!");
                taiDuLieuTaiKhoan(); xoaRongForm();
            } else JOptionPane.showMessageDialog(this, "❌ Xóa thất bại!");
        }
    }
    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
