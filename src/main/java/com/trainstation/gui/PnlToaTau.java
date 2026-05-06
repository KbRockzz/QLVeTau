package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.GheDAO;
import com.trainstation.dao.ToaTauDAO;
import com.trainstation.model.ToaTau;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Panel quản lý toa tàu
 */
public class PnlToaTau extends JPanel {
    // Regex (có thể chỉnh lại cho phù hợp quy tắc mã toa bên bạn)
    private static final Pattern PATTERN_MA_TOA = Pattern.compile("^TOA\\d{3}$");
    private static final Pattern PATTERN_LOAI_TOA = Pattern.compile("^[\\p{L}0-9\\s\\-]{1,100}$");
    private static final Pattern PATTERN_TRANG_THAI = Pattern.compile("^[\\p{L}0-9\\s\\-]{0,50}$");

    private final ToaTauDAO toaTauDAO;
    private final GheDAO gheDAO;

    private JTable bangToa;
    private DefaultTableModel modelBang;
    private JButton btnLamMoi, btnThem, btnSua, btnXoa;
    private JTextField txtMaToa, txtLoaiToa, txtNamSX, txtSucChua;
    private JComboBox<String> cboTrangThai;

    public PnlToaTau() {
        this.toaTauDAO = ToaTauDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        initComponents();
        taiDuLieuToa();
        xoaForm(); // khởi tạo form rỗng
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("QUẢN LÝ TOA TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Panel nhập liệu
        JPanel pnlInput = new JPanel(new GridBagLayout());
        pnlInput.setBorder(BorderFactory.createTitledBorder("Thông tin toa tàu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hàng 0: Mã toa - Loại toa
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        pnlInput.add(new JLabel("Mã toa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtMaToa = new JTextField(15);
        pnlInput.add(txtMaToa, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Loại toa:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        txtLoaiToa = new JTextField(15);
        pnlInput.add(txtLoaiToa, gbc);

        // Hàng 1: Năm SX - Trạng thái
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        pnlInput.add(new JLabel("Năm SX:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        txtNamSX = new JTextField(15);
        pnlInput.add(txtNamSX, gbc);

        gbc.gridx = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 3; gbc.weightx = 1;
        cboTrangThai = new JComboBox<>(new String[]{"Hoạt động", "Bảo trì", "Ngưng sử dụng"});
        pnlInput.add(cboTrangThai, gbc);

        // Hàng 2: Sức chứa
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0;
        pnlInput.add(new JLabel("Sức chứa:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.gridwidth = 3;
        txtSucChua = new JTextField(15);
        pnlInput.add(txtSucChua, gbc);
        gbc.gridwidth = 1;

        // Bảng
        String[] tenCot = {"Mã toa", "Loại toa", "Năm SX", "Trạng thái", "Sức chứa"};
        modelBang = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangToa = new JTable(modelBang);
        bangToa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bangToa.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && bangToa.getSelectedRow() != -1) {
                hienThiThongTinToa();
            }
        });
        JScrollPane scrollPane = new JScrollPane(bangToa);
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 30);

        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        pnlCenter.add(pnlInput, BorderLayout.NORTH);
        pnlCenter.add(scrollPane, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);

        // Các nút
        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> themToa());
        MaterialInitializer.styleButton(btnThem);
        pnlButton.add(btnThem);

        btnSua = new JButton("Sửa");
        btnSua.addActionListener(e -> suaToa());
        MaterialInitializer.styleButton(btnSua);
        pnlButton.add(btnSua);

        btnXoa = new JButton("Xóa");
        btnXoa.addActionListener(e -> xoaToa());
        MaterialInitializer.styleButton(btnXoa);
        pnlButton.add(btnXoa);

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            taiDuLieuToa();
            xoaForm();
        });
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDuLieuToa() {
        modelBang.setRowCount(0);
        List<ToaTau> danhSach = toaTauDAO.getAll();
        for (ToaTau t : danhSach) {
            modelBang.addRow(new Object[]{
                    t.getMaToa(),
                    t.getLoaiToa(),
                    t.getSamSX(),
                    t.getTrangThai(),
                    t.getSucChua()
            });
        }
    }

    private void hienThiThongTinToa() {
        int selectedRow = bangToa.getSelectedRow();
        if (selectedRow != -1) {
            txtMaToa.setText(String.valueOf(modelBang.getValueAt(selectedRow, 0)));
            txtLoaiToa.setText(valueOrEmpty(modelBang.getValueAt(selectedRow, 1)));
            txtNamSX.setText(valueOrEmpty(modelBang.getValueAt(selectedRow, 2)));
            cboTrangThai.setSelectedItem(valueOrDefault(modelBang.getValueAt(selectedRow, 3), "Hoạt động"));
            txtSucChua.setText(valueOrEmpty(modelBang.getValueAt(selectedRow, 4)));
            txtMaToa.setEditable(false);
        }
    }

    private String valueOrEmpty(Object o) {
        return o != null ? o.toString() : "";
    }

    private String valueOrDefault(Object o, String def) {
        return o != null ? o.toString() : def;
    }

    private void xoaForm() {
        txtMaToa.setText("");
        txtLoaiToa.setText("");
        txtNamSX.setText("");
        txtSucChua.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtMaToa.setEditable(true);
        bangToa.clearSelection();
    }

    private void themToa() {
        try {
            String maToa = txtMaToa.getText().trim();
            String loaiToa = txtLoaiToa.getText().trim();
            String namSXStr = txtNamSX.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem().toString();
            String sucChuaStr = txtSucChua.getText().trim();

            if (maToa.isEmpty() || loaiToa.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập mã toa và loại toa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!kiemTraHopLe(maToa, loaiToa, trangThai, namSXStr, sucChuaStr)) {
                return;
            }

            // Kiểm tra trùng mã toa
            if (toaTauDAO.findById(maToa) != null) {
                JOptionPane.showMessageDialog(this,
                        "Mã toa đã tồn tại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            Integer namSX = null;
            if (!namSXStr.isEmpty()) {
                namSX = Integer.valueOf(namSXStr);
            }
            Integer sucChua = null;
            if (!sucChuaStr.isEmpty()) {
                sucChua = Integer.valueOf(sucChuaStr);
            }

            ToaTau toa = new ToaTau(maToa, loaiToa, namSX, trangThai, sucChua);

            if (toaTauDAO.insert(toa)) {
                // Auto-generate seats for the new coach
                if (sucChua != null && sucChua > 0) {
                    gheDAO.insertBatch(maToa, loaiToa, sucChua);
                }
                JOptionPane.showMessageDialog(this,
                        "Đã thêm toa tàu mới thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuToa();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Thêm toa tàu thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Năm SX và Sức chứa phải là số nguyên.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void suaToa() {
        try {
            int selectedRow = bangToa.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn toa cần sửa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maToa = txtMaToa.getText().trim();
            String loaiToa = txtLoaiToa.getText().trim();
            String namSXStr = txtNamSX.getText().trim();
            String trangThai = cboTrangThai.getSelectedItem().toString();
            String sucChuaStr = txtSucChua.getText().trim();

            if (maToa.isEmpty() || loaiToa.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập mã toa và loại toa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!kiemTraHopLe(maToa, loaiToa, trangThai, namSXStr, sucChuaStr)) {
                return;
            }

            Integer namSX = null;
            if (!namSXStr.isEmpty()) {
                namSX = Integer.valueOf(namSXStr);
            }
            Integer sucChua = null;
            if (!sucChuaStr.isEmpty()) {
                sucChua = Integer.valueOf(sucChuaStr);
            }

            ToaTau toa = new ToaTau(maToa, loaiToa, namSX, trangThai, sucChua);

            if (toaTauDAO.update(toa)) {
                // Add any missing seats if capacity was increased
                if (sucChua != null && sucChua > 0) {
                    gheDAO.insertBatch(maToa, loaiToa, sucChua);
                }
                JOptionPane.showMessageDialog(this,
                        "Cập nhật thông tin toa tàu thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDuLieuToa();
                xoaForm();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Cập nhật toa tàu thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Năm SX và Sức chứa phải là số nguyên.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra: " + e.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void xoaToa() {
        try {
            int selectedRow = bangToa.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng chọn toa cần xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String maToa = modelBang.getValueAt(selectedRow, 0).toString();
            String loaiToa = String.valueOf(modelBang.getValueAt(selectedRow, 1));

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa toa " + loaiToa + " (Mã: " + maToa + ") không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                if (toaTauDAO.delete(maToa)) {
                    JOptionPane.showMessageDialog(this,
                            "Toa tàu đã được xóa thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                    taiDuLieuToa();
                    xoaForm();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Không thể xóa toa tàu!",
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

    private boolean kiemTraHopLe(String maToa, String loaiToa, String trangThai,
                                 String namSXStr, String sucChuaStr) {
        // Mã toa
        if (!PATTERN_MA_TOA.matcher(maToa).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Mã toa không hợp lệ (VD: TOA001)",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Loại toa
        if (!PATTERN_LOAI_TOA.matcher(loaiToa).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Loại toa chỉ được chứa chữ, số, khoảng trắng, dấu gạch nối và tối đa 100 ký tự.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Trạng thái
        if (!PATTERN_TRANG_THAI.matcher(trangThai).matches()) {
            JOptionPane.showMessageDialog(this,
                    "Trạng thái không hợp lệ.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Năm SX (nếu có)
        if (!namSXStr.isEmpty()) {
            try {
                int nam = Integer.parseInt(namSXStr);
                if (nam < 1900 || nam > 2100) {
                    JOptionPane.showMessageDialog(this,
                            "Năm sản xuất phải trong khoảng 1900-2100.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Năm sản xuất phải là số nguyên.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        // Sức chứa (nếu có)
        if (!sucChuaStr.isEmpty()) {
            try {
                int sc = Integer.parseInt(sucChuaStr);
                if (sc < 0 || sc > 1000) {
                    JOptionPane.showMessageDialog(this,
                            "Sức chứa phải từ 0 đến 1000.",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                        "Sức chứa phải là số nguyên.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }

        return true;
    }
}