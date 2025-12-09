package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.NhanVienDAO;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Panel hoàn vé
 */
public class PnlHoanVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private NhanVienDAO nhanVienDAO;

    private JTable bangVe;
    private DefaultTableModel modelBangVe;
    private JButton btnGuiYeuCau, btnDuyetYeuCau, btnLamMoi;
    private boolean isManager;

    private JTextField txtMaVe;
    private JDateChooser dateChooser;
    private JComboBox<String> cbTrangThai;
    private JButton btnTimKiem;
    private JButton btnXoaTimKiem;

    private JButton btnDuyetTatCa;

    public PnlHoanVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        this.nhanVienDAO = NhanVienDAO.getInstance();
        this.isManager = taiKhoan.isManager();
        initComponents();
        taiDanhSachVe();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("HOÀN VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        add(lblTieuDe, BorderLayout.NORTH);

        // filter
        JPanel pnlSearch = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));

        pnlSearch.add(new JLabel("Mã vé:"));
        txtMaVe = new JTextField(12);
        pnlSearch.add(txtMaVe);

        pnlSearch.add(new JLabel("Ngày đi:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        pnlSearch.add(dateChooser);

        pnlSearch.add(new JLabel("Trạng thái:"));
        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả",
                "Đã đặt",
                "Đã thanh toán",
                "Chờ duyệt"
        });
        cbTrangThai.setSelectedIndex(0);

        cbTrangThai.addActionListener(e -> applyFilters());
        pnlSearch.add(cbTrangThai);

        btnTimKiem = new JButton("Tìm");
        btnTimKiem.addActionListener(e -> applyFilters());
        MaterialInitializer.styleButton(btnTimKiem);
        pnlSearch.add(btnTimKiem);

        btnXoaTimKiem = new JButton("Xóa tìm");
        btnXoaTimKiem.addActionListener(e -> {
            clearSearchFields();
            applyFilters();
        });
        MaterialInitializer.styleButton(btnXoaTimKiem);
        pnlSearch.add(btnXoaTimKiem);

        add(pnlSearch, BorderLayout.BEFORE_FIRST_LINE);

        // table
        String[] tenCot = {"Mã vé", "Chuyến", "Ga đi", "Ga đến", "Giờ đi", "Ghế", "Trạng thái"};
        modelBangVe = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        bangVe = new JTable(modelBangVe);
        JScrollPane scrollVe = new JScrollPane(bangVe);
        scrollVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));
        add(scrollVe, BorderLayout.CENTER);

        // panel nút - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnGuiYeuCau = new JButton("Gửi yêu cầu hoàn vé");
        btnGuiYeuCau.addActionListener(e -> guiYeuCauHoanVe());
        MaterialInitializer.styleButton(btnGuiYeuCau);
        pnlButton.add(btnGuiYeuCau);

        if (isManager) {
            btnDuyetYeuCau = new JButton("Duyệt yêu cầu");
            btnDuyetYeuCau.addActionListener(e -> duyetYeuCauHoanVe());
            MaterialInitializer.styleButton(btnDuyetYeuCau);
            pnlButton.add(btnDuyetYeuCau);

            btnDuyetTatCa = new JButton("Duyệt tất cả");
            btnDuyetTatCa.addActionListener(e -> duyetTatCaYeuCau());
            MaterialInitializer.styleButton(btnDuyetTatCa);
            pnlButton.add(btnDuyetTatCa);
        }

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            clearAllFields();
            applyFilters();
        });
        MaterialInitializer.styleButton(btnLamMoi);
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDanhSachVe() {
        // Default behavior: no search values, status = "Tất cả"
        clearAllFields();
        applyFilters();
    }

    private void applyFilters() {
        modelBangVe.setRowCount(0);
        List<Ve> danhSach = veService.layTatCaVe();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String qMaVe = txtMaVe.getText().trim();

        Date selected = dateChooser.getDate();
        LocalDate qNgayDi = null;
        if (selected != null) {
            qNgayDi = selected.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }

        String selTrangThai = (String) cbTrangThai.getSelectedItem();
        String qTrangThai = (selTrangThai != null && !"Tất cả".equals(selTrangThai)) ? selTrangThai : null;

        boolean isAllEmpty = qMaVe.isEmpty() && qNgayDi == null && qTrangThai == null;

        for (Ve ve : danhSach) {
            String trangThai = ve.getTrangThai() != null ? ve.getTrangThai() : "";

            if (isAllEmpty) {
                if (!"Đã đặt".equals(trangThai) && !"Đã thanh toán".equals(trangThai) &&
                        !"Chờ duyệt".equals(trangThai)) {
                    continue;
                }
            }

            if (!qMaVe.isEmpty()) {
                String maVe = ve.getMaVe() != null ? ve.getMaVe() : "";
                if (!maVe.toLowerCase().contains(qMaVe.toLowerCase())) continue;
            }

            if (qNgayDi != null) {
                if (ve.getGioDi() == null || !ve.getGioDi().toLocalDate().equals(qNgayDi)) continue;
            }

            if (qTrangThai != null && !qTrangThai.equals(trangThai)) continue;

            modelBangVe.addRow(new Object[]{
                    ve.getMaVe(),
                    ve.getMaChuyen(),
                    ve.getGaDi(),
                    ve.getGaDen(),
                    ve.getGioDi() != null ? ve.getGioDi().format(formatter) : "",
                    ve.getMaSoGhe(),
                    ve.getTrangThai()
            });
        }
    }

    private void clearSearchFields() {
        txtMaVe.setText("");
        dateChooser.setDate(null);
    }

    private void clearAllFields() {
        txtMaVe.setText("");
        dateChooser.setDate(null);
        cbTrangThai.setSelectedIndex(0);
    }

    private void guiYeuCauHoanVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần hoàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maVe = (String) modelBangVe.getValueAt(row, 0);
        String trangThai = (String) modelBangVe.getValueAt(row, 6);

        if ("Chờ duyệt".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Vé đã được gửi yêu cầu hoàn!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn gửi yêu cầu hoàn vé này không?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                veService.guiYeuCauHoanVe(maVe);
                JOptionPane.showMessageDialog(this, "Gửi yêu cầu hoàn vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                applyFilters();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void duyetYeuCauHoanVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn yêu cầu cần duyệt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String maVe = (String) modelBangVe.getValueAt(row, 0);
        String trangThai = (String) modelBangVe.getValueAt(row, 6);

        if (!"Chờ duyệt".equals(trangThai)) {
            JOptionPane.showMessageDialog(this, "Vé không trong trạng thái chờ duyệt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] options = {"Chấp nhận", "Từ chối", "Hủy"};
        int choice = JOptionPane.showOptionDialog(this,
                "Duyệt yêu cầu hoàn vé " + maVe + "?",
                "Duyệt yêu cầu",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        if (choice == 0 || choice == 1) {
            try {
                boolean chapNhan = (choice == 0);
                veService.duyetHoanVe(maVe, chapNhan);
                String message = chapNhan ? "Đã chấp nhận hoàn vé!" : "Đã từ chối hoàn vé!";
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                applyFilters();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void duyetTatCaYeuCau() {
        List<Ve> allVe = veService.layTatCaVe();
        List<String> pendingMaVe = new ArrayList<>();
        for (Ve v : allVe) {
            if ("Chờ duyệt".equals(v.getTrangThai())) {
                pendingMaVe.add(v.getMaVe());
            }
        }

        if (pendingMaVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có yêu cầu chờ duyệt.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn chấp nhận tất cả (" + pendingMaVe.size() + ") yêu cầu hoàn vé không?",
                "Xác nhận duyệt tất cả",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        int success = 0;
        int failed = 0;
        List<String> failedList = new ArrayList<>();

        for (String maVe : pendingMaVe) {
            try {
                boolean ok = veService.duyetHoanVe(maVe, true); // accept
                if (ok) success++;
                else {
                    failed++;
                    failedList.add(maVe);
                }
            } catch (Exception ex) {
                failed++;
                failedList.add(maVe);
            }
        }

        String summary = "Hoàn tất duyệt: " + success + " thành công, " + failed + " thất bại.";
        if (!failedList.isEmpty()) {
            summary += "\nMã vé thất bại: " + String.join(", ", failedList);
        }
        JOptionPane.showMessageDialog(this, summary, "Kết quả", JOptionPane.INFORMATION_MESSAGE);
        applyFilters();
    }
}