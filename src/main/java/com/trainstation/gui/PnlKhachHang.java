package com.trainstation.gui;

import com.trainstation.service.KhachHangService;
import com.trainstation.model.KhachHang;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel quản lý khách hàng
 */
public class PnlKhachHang extends JPanel {
    private KhachHangService khachHangService;
    private JTable bangKhachHang;
    private DefaultTableModel modelBang;
    private JTextField txtMaKH, txtTenKH, txtEmail, txtSDT;
    private JTextField txtTimKiem;
    private JButton btnThem, btnCapNhat, btnXoa, btnLamMoi, btnTimKiem;

    public PnlKhachHang() {
        this.khachHangService = KhachHangService.getInstance();
        initComponents();
        taiDuLieuKhachHang();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Title
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Search panel
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTimKiem.add(new JLabel("Tìm theo SĐT:"));
        txtTimKiem = new JTextField(20);
        pnlTimKiem.add(txtTimKiem);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemTheoSoDienThoai());
        pnlTimKiem.add(btnTimKiem);
        
        JPanel pnlTren = new JPanel(new BorderLayout());
        pnlTren.add(lblTieuDe, BorderLayout.NORTH);
        pnlTren.add(pnlTimKiem, BorderLayout.SOUTH);
        
        add(pnlTren, BorderLayout.NORTH);

        // Table
        String[] tenCot = {"Mã KH", "Tên khách hàng", "Email", "Số điện thoại"};
        modelBang = new DefaultTableModel(tenCot, 0);
        bangKhachHang = new JTable(modelBang);
        JScrollPane scrollPane = new JScrollPane(bangKhachHang);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1;
        txtMaKH = new JTextField(20);
        pnlForm.add(txtMaKH, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Tên KH:"), gbc);
        gbc.gridx = 1;
        txtTenKH = new JTextField(20);
        pnlForm.add(txtTenKH, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        pnlForm.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 1;
        txtSDT = new JTextField(20);
        pnlForm.add(txtSDT, gbc);

        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        btnThem = new JButton("Thêm");
        btnCapNhat = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        btnThem.addActionListener(e -> themKhachHang());
        btnCapNhat.addActionListener(e -> capNhatKhachHang());
        btnXoa.addActionListener(e -> xoaKhachHang());
        btnLamMoi.addActionListener(e -> taiDuLieuKhachHang());

        pnlButton.add(btnThem);
        pnlButton.add(btnCapNhat);
//        pnlButton.add(btnXoa);
        pnlButton.add(btnLamMoi);

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.add(pnlForm, BorderLayout.CENTER);
        pnlDuoi.add(pnlButton, BorderLayout.SOUTH);

        add(pnlDuoi, BorderLayout.SOUTH);

        // Table selection listener
        bangKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangKhachHang.getSelectedRow() != -1) {
                int row = bangKhachHang.getSelectedRow();
                txtMaKH.setText(modelBang.getValueAt(row, 0).toString());
                txtTenKH.setText(modelBang.getValueAt(row, 1).toString());
                txtEmail.setText(modelBang.getValueAt(row, 2).toString());
                txtSDT.setText(modelBang.getValueAt(row, 3).toString());
            }
        });
    }

    private void taiDuLieuKhachHang() {
        modelBang.setRowCount(0);
        List<KhachHang> danhSach = khachHangService.layTatCaKhachHang();
        for (KhachHang kh : danhSach) {
            modelBang.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getTenKhachHang(),
                kh.getEmail(),
                kh.getSoDienThoai()
            });
        }
    }

    private void themKhachHang() {
        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (maKH.isEmpty() || tenKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhachHang kh = new KhachHang(maKH, tenKH, email, sdt);
        if (khachHangService.themKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            taiDuLieuKhachHang();
            xoaTrang();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần cập nhật!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String tenKH = txtTenKH.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSDT.getText().trim();

        KhachHang kh = new KhachHang(maKH, tenKH, email, sdt);
        if (khachHangService.capNhatKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
            taiDuLieuKhachHang();
            xoaTrang();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (khachHangService.xoaKhachHang(maKH)) {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
                taiDuLieuKhachHang();
                xoaTrang();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void xoaTrang() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtEmail.setText("");
        txtSDT.setText("");
    }

    private void timKiemTheoSoDienThoai() {
        String sdt = txtTimKiem.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại cần tìm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhachHang kh = khachHangService.timKhachHangTheoSoDienThoai(sdt);
        if (kh != null) {
            // Clear table and show only the found customer
            modelBang.setRowCount(0);
            modelBang.addRow(new Object[]{
                kh.getMaKhachHang(),
                kh.getTenKhachHang(),
                kh.getEmail(),
                kh.getSoDienThoai()
            });
            
            // Populate form fields
            txtMaKH.setText(kh.getMaKhachHang());
            txtTenKH.setText(kh.getTenKhachHang());
            txtEmail.setText(kh.getEmail());
            txtSDT.setText(kh.getSoDienThoai());
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại: " + sdt, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
