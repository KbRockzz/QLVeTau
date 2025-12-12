package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.service.GaService;
import com.trainstation.model.Ga;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Panel quản lý ga
 */
public class PnlGa extends JPanel {
    // Regex
    private static final Pattern PATTERN_MA_GA = Pattern.compile("^GA\\d{3}$");
    private static final Pattern PATTERN_TEN_GA = Pattern.compile("^[\\p{L}0-9\\s\\-]{1,100}$");
    private static final Pattern PATTERN_MO_TA = Pattern.compile("^.{0,200}$");
    private static final Pattern PATTERN_DIA_CHI = Pattern.compile("^.{0,200}$");
    
    private GaService gaService;
    private JTable bangGa;
    private DefaultTableModel modelBang;
    private JButton btnLamMoi, btnThemGa, btnSuaGa, btnXoaGa;
    private JTextField txtMaGa, txtTenGa, txtMoTa, txtDiaChi;
    private JComboBox<String> cboTinhTrang;

    public PnlGa() {
        this.gaService = GaService.getInstance();
        initComponents();
        taiDuLieuGa();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ GA", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // panel nhập
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin ga"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mã ga:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMaGa = new JTextField(20);
        pnlInput.add(txtMaGa, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Tên ga:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtTenGa = new JTextField(20);
        pnlInput.add(txtTenGa, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mô tả:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMoTa = new JTextField(20);
        pnlInput.add(txtMoTa, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Tình trạng:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cboTinhTrang = new JComboBox<>(new String[]{"Hoạt động", "Bảo trì", "Tạm dừng"});
        pnlInput.add(cboTinhTrang, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtDiaChi = new JTextField(20);
        pnlInput.add(txtDiaChi, gbc);

        // Table
        String[] tenCot = {"Mã ga", "Tên ga", "Mô tả", "Tình trạng", "Địa chỉ"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangGa = new JTable(modelBang);
        bangGa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangGa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangGa.getSelectedRow() != -1) {
                hienThiThongTinGa();
            }
        });
        JScrollPane scrollPane = new JScrollPane(bangGa);
        // Giảm chiều cao bảng để form phía dưới hiển thị đầy đủ
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 30);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(pnlInput, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Các nút - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();
        
        btnThemGa = new JButton("Thêm");
        btnThemGa.addActionListener(e -> themGa());
        MaterialInitializer.styleButton(btnThemGa);
        pnlButton.add(btnThemGa);

        btnSuaGa = new JButton("Sửa");
        btnSuaGa.addActionListener(e -> suaGa());
        MaterialInitializer.styleButton(btnSuaGa);
        pnlButton.add(btnSuaGa);

        btnXoaGa = new JButton("Xóa");
        btnXoaGa.addActionListener(e -> xoaGa());
        MaterialInitializer.styleButton(btnXoaGa);
        pnlButton.add(btnXoaGa);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            taiDuLieuGa();
            xoaForm();
        });
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDuLieuGa() {
        modelBang.setRowCount(0);
        List<Ga> danhSach = gaService.layTatCaGa();
        for (Ga ga : danhSach) {
            modelBang.addRow(new Object[]{
                ga.getMaGa(),
                ga.getTenGa(),
                ga.getMoTa(),
                ga.getTinhTrang(),
                ga.getDiaChi()
            });
        }
    }

    private void hienThiThongTinGa() {
        int selectedRow = bangGa.getSelectedRow();
        if (selectedRow != -1) {
            txtMaGa.setText(modelBang.getValueAt(selectedRow, 0).toString());
            txtTenGa.setText(modelBang.getValueAt(selectedRow, 1).toString());
            Object moTa = modelBang.getValueAt(selectedRow, 2);
            txtMoTa.setText(moTa != null ? moTa.toString() : "");
            Object tinhTrang = modelBang.getValueAt(selectedRow, 3);
            cboTinhTrang.setSelectedItem(tinhTrang != null ? tinhTrang.toString() : "Hoạt động");
            Object diaChi = modelBang.getValueAt(selectedRow, 4);
            txtDiaChi.setText(diaChi != null ? diaChi.toString() : "");
            txtMaGa.setEditable(false);
        }
    }

    private void xoaForm() {
        txtMaGa.setText(gaService.taoMaGa());
        txtTenGa.setText("");
        txtMoTa.setText("");
        txtDiaChi.setText("");
        cboTinhTrang.setSelectedIndex(0);
        txtMaGa.setEditable(true);
        bangGa.clearSelection();
    }

    private void themGa() {
        try {
            // Kiểm tra thông tin nhập vào
            String maGa = txtMaGa.getText().trim();
            String tenGa = txtTenGa.getText().trim();
            String moTa = txtMoTa.getText().trim();
            String tinhTrang = cboTinhTrang.getSelectedItem().toString();
            String diaChi = txtDiaChi.getText().trim();

            if (maGa.isEmpty() || tenGa.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập mã ga và tên ga!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra bằng regex
            if (!kiemTraHopLe(maGa, tenGa, moTa, diaChi)) {
                return;
            }

            // Kiểm tra đã tồn tại chưa
            if (gaService.timGaTheoMa(maGa) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Mã ga đã tồn tại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Tạo đối tượng ga mới
            Ga ga = new Ga(maGa, tenGa, moTa, tinhTrang, diaChi, true);
            
            if (gaService.themGa(ga)) {
                JOptionPane.showMessageDialog(this, 
                    "Đã thêm ga mới thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuGa();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Thêm ga thất bại!", 
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

    private void suaGa() {
        try {
            int selectedRow = bangGa.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn ga cần sửa!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra thông tin sửa
            String maGa = txtMaGa.getText().trim();
            String tenGa = txtTenGa.getText().trim();
            String moTa = txtMoTa.getText().trim();
            String tinhTrang = cboTinhTrang.getSelectedItem().toString();
            String diaChi = txtDiaChi.getText().trim();

            if (maGa.isEmpty() || tenGa.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập mã ga và tên ga!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra bằng regex
            if (!kiemTraHopLe(maGa, tenGa, moTa, diaChi)) {
                return;
            }

            // Tạo đối tượng ga với thông tin cập nhật
            Ga ga = new Ga(maGa, tenGa, moTa, tinhTrang, diaChi, true);
            
            if (gaService.capNhatGa(ga)) {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật thông tin ga thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuGa();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Cập nhật ga thất bại!", 
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

    private boolean kiemTraHopLe(String maGa, String tenGa, String moTa, String diaChi) {
        // Kiểm tra Mã ga
        if (!PATTERN_MA_GA.matcher(maGa).matches()) {
            JOptionPane.showMessageDialog(this,
                "Mã ga không hợp lệ (VD: GA001)",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Tên ga
        if (!PATTERN_TEN_GA.matcher(tenGa).matches()) {
            JOptionPane.showMessageDialog(this,
                "Tên ga chỉ được chứa chữ, số, khoảng trắng, dấu gạch nối và tối đa 100 ký tự.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Mô tả
        if (!PATTERN_MO_TA.matcher(moTa).matches()) {
            JOptionPane.showMessageDialog(this,
                "Mô tả tối đa 200 ký tự.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Địa chỉ
        if (!PATTERN_DIA_CHI.matcher(diaChi).matches()) {
            JOptionPane.showMessageDialog(this,
                "Địa chỉ tối đa 200 ký tự.",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void xoaGa() {
        try {
            int selectedRow = bangGa.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn ga cần xóa!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maGa = modelBang.getValueAt(selectedRow, 0).toString();
            String tenGa = modelBang.getValueAt(selectedRow, 1).toString();

            // Hiển thị hộp thoại xác nhận
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa ga " + tenGa + " (Mã: " + maGa + ") không?", 
                "Xác nhận", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (gaService.xoaGa(maGa)) {
                    JOptionPane.showMessageDialog(this, 
                        "Ga đã được xóa thành công!", 
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieuGa();
                    xoaForm();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa ga!", 
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
