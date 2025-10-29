package com.trainstation.gui;

import com.trainstation.service.TauService;
import com.trainstation.model.Tau;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Panel quản lý tàu
 */
public class PnlTau extends JPanel {
    // Regex
    private static final Pattern PATTERN_MA_TAU = Pattern.compile("^T\\d{3}$");
    private static final Pattern PATTERN_TEN_TAU = Pattern.compile("^[\\p{L}0-9\\s\\-]{1,100}$");
    private static final Pattern PATTERN_SO_TOA = Pattern.compile("^([1-9]|[1-9][0-9])$");
    
    private TauService tauService;
    private JTable bangTau;
    private DefaultTableModel modelBang;
    private JButton btnLamMoi, btnThemTau, btnSuaTau, btnXoaTau;
    private JTextField txtMaTau, txtTenTau, txtSoToa;
    private JComboBox<String> cboTrangThai;

    public PnlTau() {
        this.tauService = TauService.getInstance();
        initComponents();
        taiDuLieuTau();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // pannel nhập
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin tàu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mã tàu:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMaTau = new JTextField(20);
        pnlInput.add(txtMaTau, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Tên tàu:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtTenTau = new JTextField(20);
        pnlInput.add(txtTenTau, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlInput.add(new JLabel("Số toa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtSoToa = new JTextField(20);
        pnlInput.add(txtSoToa, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Bảo trì", "Tạm dừng", "Dừng hoạt động"});
        pnlInput.add(cboTrangThai, gbc);

        // Table
        String[] tenCot = {"Mã tàu", "Tên tàu", "Số toa", "Trạng thái"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangTau = new JTable(modelBang);
        bangTau.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangTau.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangTau.getSelectedRow() != -1) {
                hienThiThongTinTau();
            }
        });
        JScrollPane scrollPane = new JScrollPane(bangTau);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(pnlInput, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Các nút
        JPanel pnlButton = new JPanel(new FlowLayout());
        
        btnThemTau = new JButton("Thêm");
        btnThemTau.addActionListener(e -> themTau());
        pnlButton.add(btnThemTau);

        btnSuaTau = new JButton("Sửa");
        btnSuaTau.addActionListener(e -> suaTau());
        pnlButton.add(btnSuaTau);

        btnXoaTau = new JButton("Xóa");
        btnXoaTau.addActionListener(e -> xoaTau());
        pnlButton.add(btnXoaTau);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            taiDuLieuTau();
            xoaForm();
        });
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDuLieuTau() {
        modelBang.setRowCount(0);
        // Không hiện thị các tàu dừng hoạt động
        List<Tau> danhSach = tauService.layTauHoatDong();
        for (Tau tau : danhSach) {
            modelBang.addRow(new Object[]{
                tau.getMaTau(),
                tau.getTenTau(),
                tau.getSoToa(),
                tau.getTrangThai()
            });
        }
    }

    private void hienThiThongTinTau() {
        int selectedRow = bangTau.getSelectedRow();
        if (selectedRow != -1) {
            txtMaTau.setText(modelBang.getValueAt(selectedRow, 0).toString());
            txtTenTau.setText(modelBang.getValueAt(selectedRow, 1).toString());
            txtSoToa.setText(modelBang.getValueAt(selectedRow, 2).toString());
            cboTrangThai.setSelectedItem(modelBang.getValueAt(selectedRow, 3).toString());
            txtMaTau.setEditable(false);
        }
    }

    private void xoaForm() {
        txtMaTau.setText(TauService.getInstance().taoMaTau());
        txtTenTau.setText("");
        txtSoToa.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtMaTau.setEditable(true);
        bangTau.clearSelection();
    }

    private void themTau() {
        try {
            // Kiểm tra thông tin nhập vào
            String maTau = txtMaTau.getText().trim();
            String tenTau = txtTenTau.getText().trim();
            String soToaStr = txtSoToa.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem().toString();

            if (maTau.isEmpty() || tenTau.isEmpty() || soToaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập đầy đủ thông tin!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra bằng regex
            if (!kiemTraHopLe(maTau, tenTau, soToaStr, false)) {
                return;
            }

            int soToa = Integer.parseInt(soToaStr);

            // Kiểm tra đã tồn tại chưa
            if (tauService.timTauTheoMa(maTau) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Mã tàu đã tồn tại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo đối tượng tàu mới
            Tau tau = new Tau(maTau, soToa, tenTau, trangThai);
            
            if (tauService.themTau(tau)) {
                JOptionPane.showMessageDialog(this, 
                    "Đã thêm tàu mới thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTau();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Thêm tàu thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaTau() {
        try {
            int selectedRow = bangTau.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn tàu cần sửa!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra thông tin sửa
            String maTau = txtMaTau.getText().trim();
            String tenTau = txtTenTau.getText().trim();
            String soToaStr = txtSoToa.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem().toString();

            if (maTau.isEmpty() || tenTau.isEmpty() || soToaStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập đầy đủ thông tin!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra bằng regex
            if (!kiemTraHopLe(maTau, tenTau, soToaStr, true)) {
                return;
            }

            int soToa = Integer.parseInt(soToaStr);

            // Tạo đối tượng tàu với thông tin cập nhật
            Tau tau = new Tau(maTau, soToa, tenTau, trangThai);
            
            if (tauService.capNhatTau(tau)) {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật thông tin tàu thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuTau();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật tàu thất bại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean kiemTraHopLe(String maTau, String tenTau, String soToaStr, boolean isUpdate) {
        // Kiểm tra Mã tàu
        if (!PATTERN_MA_TAU.matcher(maTau).matches()) {
            JOptionPane.showMessageDialog(this,
                "Mã tàu không hợp lệ (VD: T001)",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Tên tàu
        if (!PATTERN_TEN_TAU.matcher(tenTau).matches()) {
            JOptionPane.showMessageDialog(this,
                "Tên tàu chỉ được chứa chữ, số, khoảng trắng, dấu gạch nối và tối đa 100 ký tự.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Số toa
        if (!PATTERN_SO_TOA.matcher(soToaStr).matches()) {
            JOptionPane.showMessageDialog(this,
                "Số toa phải là số từ 1 đến 99.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void xoaTau() {
        try {
            int selectedRow = bangTau.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn tàu cần xóa!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maTau = modelBang.getValueAt(selectedRow, 0).toString();
            String tenTau = modelBang.getValueAt(selectedRow, 1).toString();

            // Hiển thị hộp thoại xác nhận
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn dừng hoạt động tàu " + tenTau + " (Mã: " + maTau + ") không?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (tauService.dungHoatDongTau(maTau)) {
                    JOptionPane.showMessageDialog(this, 
                        "Tàu đã được chuyển sang trạng thái dừng hoạt động!", 
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieuTau();
                    xoaForm();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể dừng hoạt động tàu!", 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
