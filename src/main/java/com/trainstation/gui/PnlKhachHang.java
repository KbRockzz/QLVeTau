package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
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

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));

        // panel tìm kiếm
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTimKiem.add(new JLabel("Tìm theo SĐT:"));
        txtTimKiem = new JTextField(20);
        pnlTimKiem.add(txtTimKiem);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemTheoSoDienThoai());
        MaterialInitializer.styleButton(btnTimKiem);
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
        // Giảm chiều cao bảng để form phía dưới hiển thị đầy đủ
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 35);
        add(scrollPane, BorderLayout.CENTER);

        // panel form - 2 cột song song giống Quản lý Nhân viên
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Cột 1
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.0; // Label không mở rộng
        pnlForm.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5; // TextField mở rộng
        txtMaKH = new JTextField(20);
        pnlForm.add(txtMaKH, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.0;
        pnlForm.add(new JLabel("Tên KH:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.5;
        txtTenKH = new JTextField(20);
        pnlForm.add(txtTenKH, gbc);

        // Cột 2
        gbc.gridx = 2; gbc.gridy = 0;
        gbc.weightx = 0.0;
        pnlForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        txtEmail = new JTextField(20);
        pnlForm.add(txtEmail, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        gbc.weightx = 0.0;
        pnlForm.add(new JLabel("SĐT:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 0.5;
        txtSDT = new JTextField(20);
        pnlForm.add(txtSDT, gbc);

        // Các nút - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();
        btnThem = new JButton("Thêm");
        btnCapNhat = new JButton("Cập nhật");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        btnThem.addActionListener(e -> themKhachHang());
        MaterialInitializer.styleButton(btnThem);
        
        btnCapNhat.addActionListener(e -> capNhatKhachHang());
        MaterialInitializer.styleButton(btnCapNhat);
        
        btnXoa.addActionListener(e -> xoaKhachHang());
        MaterialInitializer.styleButton(btnXoa);
        
        btnLamMoi.addActionListener(e -> taiDuLieuKhachHang());
        MaterialInitializer.styleButton(btnLamMoi);

        pnlButton.add(btnThem);
        pnlButton.add(btnCapNhat);
        pnlButton.add(btnXoa);
        pnlButton.add(btnLamMoi);

        JPanel pnlDuoi = new JPanel(new BorderLayout());
        pnlDuoi.add(pnlForm, BorderLayout.CENTER);
        pnlDuoi.add(pnlButton, BorderLayout.SOUTH);
        pnlDuoi.setPreferredSize(new Dimension(0, 220)); // Đảm bảo có đủ không gian
        add(pnlDuoi, BorderLayout.SOUTH);

        // Chọn bảng để hiện thông tin trên mấy cái txt
        bangKhachHang.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangKhachHang.getSelectedRow() != -1) {
                int row = bangKhachHang.getSelectedRow();
                txtMaKH.setText(modelBang.getValueAt(row, 0).toString());
                txtTenKH.setText(modelBang.getValueAt(row, 1).toString());
                txtEmail.setText(modelBang.getValueAt(row, 2).toString());
                txtSDT.setText(modelBang.getValueAt(row, 3).toString());
                txtMaKH.setEditable(false);
            }
        });
    }

    private void xoaForm() {
        txtMaKH.setText(KhachHangService.getInstance().taoMaKhachHang());
        txtTenKH.setText("");
        txtEmail.setText("");
        txtSDT.setText("");
        txtTenKH.setEditable(true);
        bangKhachHang.clearSelection();
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
        xoaForm();
    }

    private boolean validateKhachHang() {
        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim().replaceAll("\\s+", " ");
        String email = txtEmail.getText().trim();
        String sdt = txtSDT.getText().trim();

        if (!maKH.matches("^[A-Z]{2}[0-9]{2,}$")) {
            JOptionPane.showMessageDialog(this, "Mã KH chỉ gồm 2 kí tự hoa theo sau là số");
            return false;
        }

        if (!tenKH.matches("^\\p{Lu}\\p{Ll}*(\\s\\p{Lu}\\p{Ll}*)*$")) {
            JOptionPane.showMessageDialog(this, "Tên KH phải viết hoa chữ đầu mỗi từ và có ít nhất 2 từ (2–50 ký tự).");
            return false;
        }

        if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            JOptionPane.showMessageDialog(this, "Email không hợp lệ! (ví dụ: abc@gmail.com)");
            return false;
        }

        if (!sdt.matches("^(0[3|5|7|8|9])[0-9]{8}$")) {
            JOptionPane.showMessageDialog(this, "Số điện thoại Việt Nam không hợp lệ (10 số, đầu 03-09)!");
            return false;
        }

       
        txtTenKH.setText(tenKH);
        return true;
    }

    private void themKhachHang() {
        if (!validateKhachHang()) return;

        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSDT.getText().trim();

        KhachHang kh = new KhachHang(maKH, tenKH, email, sdt, true);
        if (khachHangService.themKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
            taiDuLieuKhachHang();
            xoaTrang();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void capNhatKhachHang() {
        if (!validateKhachHang()) return;

        String maKH = txtMaKH.getText().trim();
        String tenKH = txtTenKH.getText().trim();
        String email = txtEmail.getText().trim();
        String sdt = txtSDT.getText().trim();

        KhachHang kh = new KhachHang(maKH, tenKH, email, sdt, true);
        if (khachHangService.capNhatKhachHang(kh)) {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
            taiDuLieuKhachHang();
            xoaTrang();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaTrang() {
        txtMaKH.setText("");
        txtTenKH.setText("");
        txtEmail.setText("");
        txtSDT.setText("");
        txtMaKH.setEditable(true);
        bangKhachHang.clearSelection();
    }

    private void timKiemTheoSoDienThoai() {
        String sdt = txtTimKiem.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại cần tìm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        KhachHang kh = khachHangService.timKhachHangTheoSoDienThoai(sdt);
        if (kh != null) {
            modelBang.setRowCount(0);
            modelBang.addRow(new Object[]{
                    kh.getMaKhachHang(),
                    kh.getTenKhachHang(),
                    kh.getEmail(),
                    kh.getSoDienThoai()
            });

            txtMaKH.setText(kh.getMaKhachHang());
            txtTenKH.setText(kh.getTenKhachHang());
            txtEmail.setText(kh.getEmail());
            txtSDT.setText(kh.getSoDienThoai());
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại: " + sdt, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Xóa khách hàng (soft delete)
     */
    private void xoaKhachHang() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng " + maKH + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
        
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
}
