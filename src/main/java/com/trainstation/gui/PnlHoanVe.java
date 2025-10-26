package com.trainstation.gui;

import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.NhanVienDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    // New: filter combo box for status
    private JComboBox<String> cbTrangThai;

    // New: approve-all button
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
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel contains title and filter
        JLabel lblTieuDe = new JLabel("HOÀN VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));

        // Filter panel (right-aligned)
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Lọc trạng thái:"));

        cbTrangThai = new JComboBox<>(new String[]{
                "Tất cả",
                "Đã đặt",
                "Đã thanh toán",
                "Chờ duyệt"
        });
        cbTrangThai.setSelectedIndex(0);
        // Refresh table when selection changes
        cbTrangThai.addActionListener(e -> taiDanhSachVe());
        filterPanel.add(cbTrangThai);

        // Combine title and filter in a top container
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(lblTieuDe, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        // Ticket table
        String[] tenCot = {"Mã vé", "Chuyến", "Ga đi", "Ga đến", "Giờ đi", "Ghế", "Trạng thái"};
        modelBangVe = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangVe = new JTable(modelBangVe);
        JScrollPane scrollVe = new JScrollPane(bangVe);
        scrollVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));
        add(scrollVe, BorderLayout.CENTER);

        // Button panel
        JPanel pnlButton = new JPanel(new FlowLayout());

        btnGuiYeuCau = new JButton("Gửi yêu cầu hoàn vé");
        btnGuiYeuCau.addActionListener(e -> guiYeuCauHoanVe());
        pnlButton.add(btnGuiYeuCau);

        if (isManager) {
            btnDuyetYeuCau = new JButton("Duyệt yêu cầu");
            btnDuyetYeuCau.addActionListener(e -> duyetYeuCauHoanVe());
            pnlButton.add(btnDuyetYeuCau);

            // New button: approve all pending requests
            btnDuyetTatCa = new JButton("Duyệt tất cả");
            btnDuyetTatCa.addActionListener(e -> duyetTatCaYeuCau());
            pnlButton.add(btnDuyetTatCa);
        }

        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> {
            cbTrangThai.setSelectedIndex(0); // reset filter to "Tất cả"
            taiDanhSachVe();
        });
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    /**
     * Load ticket list and apply current status filter from cbTrangThai
     */
    private void taiDanhSachVe() {
        modelBangVe.setRowCount(0);
        List<Ve> danhSach = veService.layTatCaVe();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String filter = null;
        if (cbTrangThai != null) {
            String sel = (String) cbTrangThai.getSelectedItem();
            if (sel != null && !"Tất cả".equals(sel)) {
                filter = sel;
            }
        }

        for (Ve ve : danhSach) {
            // Show tickets that can be refunded or are pending approval
            String trangThai = ve.getTrangThai();

            // If a filter is set, only include matching status
            if (filter != null) {
                if (!filter.equals(trangThai)) {
                    continue;
                }
            } else {
                // If no filter, show only relevant statuses
                if (!"Đã đặt".equals(trangThai) && !"Đã thanh toán".equals(trangThai) &&
                        !"Chờ duyệt".equals(trangThai)) {
                    continue;
                }
            }

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
                taiDanhSachVe();
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
                boolean chấpNhan = (choice == 0);
                veService.duyetHoanVe(maVe, chấpNhan);
                String message = chấpNhan ? "Đã chấp nhận hoàn vé!" : "Đã từ chối hoàn vé!";
                JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDanhSachVe();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Approve all pending refund requests ("Chờ duyệt").
     * Only available to managers.
     */
    private void duyetTatCaYeuCau() {
        // Collect all pending ticket IDs (we process all in the data source, not only filtered view)
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
        taiDanhSachVe();
    }
}