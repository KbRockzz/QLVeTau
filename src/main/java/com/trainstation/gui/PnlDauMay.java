package com.trainstation.gui;

import com.toedter.calendar.JDateChooser;
import com.trainstation.config.MaterialInitializer;
import com.trainstation.service.DauMayService;
import com.trainstation.model.DauMay;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Panel quản lý đầu máy
 */
public class PnlDauMay extends JPanel {
    // Regex
    private static final Pattern PATTERN_MA_DAUMAY = Pattern.compile("^DM\\d{3}$");
    private static final Pattern PATTERN_TEN_DAUMAY = Pattern.compile("^[\\p{L}0-9\\s\\-]{1,100}$");

    private DauMayService dauMayService;
    private JTable bangDauMay;
    private DefaultTableModel modelBang;
    private JButton btnLamMoi, btnThemDauMay, btnSuaDauMay, btnXoaDauMay,btnDungDauMay;
    private JTextField txtMaDauMay, txtTenDauMay, txtLoaiDauMay, txtNamSX;
    private JDateChooser dateChooserLanBaoTri;
    private JComboBox<String> cboTrangThai;

    public PnlDauMay() {
        this.dauMayService = DauMayService.getInstance();
        initComponents();
        taiDuLieuDauMay();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ ĐẦU MÁY", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // pannel nhập
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin đầu máy"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mã đầu máy:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMaDauMay = new JTextField(20);
        pnlInput.add(txtMaDauMay, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Loại đầu máy:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtLoaiDauMay = new JTextField(20);
        pnlInput.add(txtLoaiDauMay, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlInput.add(new JLabel("Tên đầu máy:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtTenDauMay = new JTextField(20);
        pnlInput.add(txtTenDauMay, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cboTrangThai = new JComboBox<>(new String[]{"Sẵn sàng", "Bảo trì", "Tạm dừng", "Dừng hoạt động"});
        pnlInput.add(cboTrangThai, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Năm sản xuất"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNamSX = new JTextField(20);
        pnlInput.add(txtNamSX, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Lần bảo trì gần nhất:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        dateChooserLanBaoTri = new JDateChooser();
        pnlInput.add(dateChooserLanBaoTri, gbc);


        // Table
        String[] tenCot = {"Mã đầu máy", "Loại đầu máy", "Tên đầu máy", "Năm sản xuất", "Lần bảo trì gần nhất", "Trạng thái"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangDauMay = new JTable(modelBang);
        bangDauMay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangDauMay.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangDauMay.getSelectedRow() != -1) {
                hienThiThongTinDauMay();
            }
        });
        JScrollPane scrollPane = new JScrollPane(bangDauMay);
        // Giảm chiều cao bảng để form phía dưới hiển thị đầy đủ
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 30);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(pnlInput, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Các nút - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnThemDauMay = new JButton("Thêm");
        btnThemDauMay.addActionListener(e -> themDauMay());
        MaterialInitializer.styleButton(btnThemDauMay);
        pnlButton.add(btnThemDauMay);

        btnSuaDauMay = new JButton("Sửa");
        btnSuaDauMay.addActionListener(e -> suaDauMay());
        MaterialInitializer.styleButton(btnSuaDauMay);
        pnlButton.add(btnSuaDauMay);

        btnDungDauMay = new JButton("Dừng");
        btnDungDauMay.addActionListener(e -> dungDauMay());
        MaterialInitializer.styleButton(btnDungDauMay);
        pnlButton.add(btnDungDauMay);

        btnXoaDauMay = new JButton("Xóa");
        btnXoaDauMay.addActionListener(e -> xoaDauMay());
        MaterialInitializer.styleButton(btnXoaDauMay);
        pnlButton.add(btnXoaDauMay);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            taiDuLieuDauMay();
            xoaForm();
        });
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void xoaDauMay() {
        int selectedRow = bangDauMay.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đầu máy cần xóa!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa đầu máy này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        dauMayService.xoaDauMay(txtMaDauMay.getText().trim());
        taiDuLieuDauMay();
    }

    private void taiDuLieuDauMay() {
        modelBang.setRowCount(0);
        List<DauMay> danhSach = dauMayService.layTatCaDauMay();
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

    private void hienThiThongTinDauMay() {
        int selectedRow = bangDauMay.getSelectedRow();
        if (selectedRow != -1) {
            txtMaDauMay.setText(modelBang.getValueAt(selectedRow, 0).toString());
            txtLoaiDauMay.setText(modelBang.getValueAt(selectedRow, 1).toString());
            txtTenDauMay.setText(modelBang.getValueAt(selectedRow, 2).toString());
            txtNamSX.setText(modelBang.getValueAt(selectedRow, 3).toString());
            Object lanBaoTriObj = modelBang.getValueAt(selectedRow, 4);
            if (lanBaoTriObj != null) {
                dateChooserLanBaoTri.setDate(MaterialInitializer.convertToDateViaInstant(
                        (java.time.LocalDateTime) lanBaoTriObj));
            } else {
                dateChooserLanBaoTri.setDate(null);
            }
            cboTrangThai.setSelectedItem(modelBang.getValueAt(selectedRow, 5).toString());
        }
    }

    private void xoaForm() {
        txtMaDauMay.setText("");
        txtLoaiDauMay.setText("");
        txtTenDauMay.setText("");
        txtNamSX.setText("");
        dateChooserLanBaoTri.setDate(null);
        cboTrangThai.setSelectedIndex(0);
        bangDauMay.clearSelection();
    }

    private void themDauMay() {
        try {
            // Kiểm tra thông tin nhập
            String maDauMay = txtMaDauMay.getText().trim();
            String loaiDauMay = txtLoaiDauMay.getText().trim();
            String tenDauMay = txtTenDauMay.getText().trim();
            String namSXStr = txtNamSX.getText().trim();
            Date lanBaoTri = dateChooserLanBaoTri.getDate();

            if (maDauMay.isEmpty() || loaiDauMay.isEmpty() || tenDauMay.isEmpty() || namSXStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kiểm tra bằng regex
            if (!kiemTraHopLe(maDauMay, tenDauMay, false)) {
                return;
            }
            if (lanBaoTri != null) {
                // Kiểm tra ngày bảo trì không được sau ngày hiện tại
                java.util.Date today = new java.util.Date();
                if (dateChooserLanBaoTri.getDate().after(today)) {
                    JOptionPane.showMessageDialog(this,
                            "Ngày bảo trì gần nhất không được sau ngày hiện tại.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            int namSX = Integer.parseInt(namSXStr);
            LocalDateTime ldt = lanBaoTri.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            DauMay dauMay = new DauMay(maDauMay, loaiDauMay, tenDauMay, namSX,
                    ldt,
                    cboTrangThai.getSelectedItem().toString());

            if (dauMayService.themDauMay(dauMay)) {
                JOptionPane.showMessageDialog(this,
                        "Thêm đầu máy thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuDauMay();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm đầu máy thất bại!",
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

    private void suaDauMay() {
        try {
            // Kiểm tra thông tin nhập
            String maDauMay = txtMaDauMay.getText().trim();
            String loaiDauMay = txtLoaiDauMay.getText().trim();
            String tenDauMay = txtTenDauMay.getText().trim();
            String namSXStr = txtNamSX.getText().trim();
            Date lanBaoTri = dateChooserLanBaoTri.getDate();
            if (maDauMay.isEmpty() || loaiDauMay.isEmpty() || tenDauMay.isEmpty() || namSXStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Kiểm tra bằng regex
            if (kiemTraHopLe(maDauMay, tenDauMay, true) == false) {
                return;
            }
            if (lanBaoTri != null) {
                // Kiểm tra ngày bảo trì không được sau ngày hiện tại
                java.util.Date today = new java.util.Date();
                if (dateChooserLanBaoTri.getDate().after(today)) {
                    JOptionPane.showMessageDialog(this,
                            "Ngày bảo trì gần nhất không được sau ngày hiện tại.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            int namSX = Integer.parseInt(namSXStr);
            LocalDateTime ldt = lanBaoTri.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            // Tạo đối tượng đầu máy mới
            DauMay dauMay = new DauMay(maDauMay, loaiDauMay, tenDauMay, namSX, ldt,
                    cboTrangThai.getSelectedItem().toString());
            if (dauMayService.capNhatDauMay(dauMay)) {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật đầu máy thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuDauMay();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật đầu máy thất bại!",
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

    private boolean kiemTraHopLe(String maDauMay, String tenDauMay, boolean isUpdate) {
        // Kiểm tra Mã đầu máy
        if (!PnlDauMay.PATTERN_MA_DAUMAY.matcher(maDauMay).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Mã đầu máy không hợp lệ (VD: T001)",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Kiểm tra Tên đầu máy
        if (!PATTERN_TEN_DAUMAY.matcher(tenDauMay).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Tên đầu máy chỉ được chứa chữ, số, khoảng trắng, dấu gạch nối và tối đa 100 ký tự.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void dungDauMay() {
        try {
            int selectedRow = bangDauMay.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn đầu máy cần xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maDauMay = modelBang.getValueAt(selectedRow, 0).toString();
            String tenDauMay = modelBang.getValueAt(selectedRow, 1).toString();

            // Hiển thị hộp thoại xác nhận
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn dừng hoạt động đầu máy " + tenDauMay + " (Mã: " + maDauMay + ") không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (dauMayService.dungHoatDongDauMay(maDauMay)) {
                    JOptionPane.showMessageDialog(this,
                            "Đầu máy đã được chuyển sang trạng thái dừng hoạt động!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieuDauMay();
                    xoaForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể dừng hoạt động đầu máy!",
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

