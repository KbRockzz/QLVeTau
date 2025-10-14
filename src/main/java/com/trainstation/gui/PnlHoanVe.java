package com.trainstation.gui;

import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.NhanVienDAO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
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

        // Title
        JLabel lblTieuDe = new JLabel("HOÀN VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

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
        }
        
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDanhSachVe());
        pnlButton.add(btnLamMoi);
        
        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDanhSachVe() {
        modelBangVe.setRowCount(0);
        List<Ve> danhSach = veService.layTatCaVe();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Ve ve : danhSach) {
            // Show tickets that can be refunded or are pending approval
            String trangThai = ve.getTrangThai();
            if ("Đã đặt".equals(trangThai) || "Đã thanh toán".equals(trangThai) || 
                "Chờ duyệt".equals(trangThai)) {
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
}
