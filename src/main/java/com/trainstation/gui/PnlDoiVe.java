package com.trainstation.gui;

import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel đổi vé
 */
public class PnlDoiVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private ChuyenTauDAO chuyenTauDAO;
    private ToaTauDAO toaTauDAO;
    private GheDAO gheDAO;
    
    private JTextField txtMaKH;
    private JButton btnTimKiem;
    private JTable bangVe;
    private DefaultTableModel modelBangVe;
    private Ve veHienTai;

    public PnlDoiVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("ĐỔI VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Search panel
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlTimKiem.add(new JLabel("Mã khách hàng:"));
        txtMaKH = new JTextField(20);
        pnlTimKiem.add(txtMaKH);
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemVe());
        pnlTimKiem.add(btnTimKiem);
        
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        
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
        pnlTop.add(scrollVe, BorderLayout.CENTER);
        
        add(pnlTop, BorderLayout.CENTER);
        
        // Button panel
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.addActionListener(e -> chonVeDeDoiVe());
        pnlButton.add(btnDoiVe);
        
        add(pnlButton, BorderLayout.SOUTH);
    }

    private void timKiemVe() {
        String maKH = txtMaKH.getText().trim();
        if (maKH.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        modelBangVe.setRowCount(0);
        List<Ve> danhSach = veService.layVeTheoKhachHang(maKH);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Ve ve : danhSach) {
            if ("Đã thanh toán".equals(ve.getTrangThai()) || "Đã đặt".equals(ve.getTrangThai())) {
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

    private void chonVeDeDoiVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần đổi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maVe = (String) modelBangVe.getValueAt(row, 0);
        veHienTai = veService.timVeTheoMa(maVe);
        
        if (veHienTai == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show dialog to select new train/seat
        hienThiDialogChonChuyenMoi();
    }

    private void hienThiDialogChonChuyenMoi() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Chọn chuyến và ghế mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlNoiDung = new JPanel(new BorderLayout(10, 10));
        pnlNoiDung.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Train selection
        JPanel pnlChonChuyen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlChonChuyen.add(new JLabel("Chọn chuyến mới:"));
        JComboBox<String> cmbChuyen = new JComboBox<>();
        List<ChuyenTau> danhSachChuyen = chuyenTauDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (ChuyenTau ct : danhSachChuyen) {
            String item = ct.getMaChuyen() + " - " + ct.getGaDi() + " → " + ct.getGaDen();
            if (ct.getGioDi() != null) {
                item += " (" + ct.getGioDi().format(formatter) + ")";
            }
            cmbChuyen.addItem(item);
        }
        pnlChonChuyen.add(cmbChuyen);
        pnlNoiDung.add(pnlChonChuyen, BorderLayout.NORTH);
        
        // Seat selection panel
        JPanel pnlGhe = new JPanel(new GridLayout(0, 5, 5, 5));
        JScrollPane scrollGhe = new JScrollPane(pnlGhe);
        scrollGhe.setBorder(BorderFactory.createTitledBorder("Chọn ghế mới"));
        pnlNoiDung.add(scrollGhe, BorderLayout.CENTER);
        
        final Ghe[] gheChon = {null};
        final ChuyenTau[] chuyenChon = {null};
        
        cmbChuyen.addActionListener(e -> {
            String selected = (String) cmbChuyen.getSelectedItem();
            if (selected == null) return;
            
            String maChuyen = selected.split(" - ")[0];
            ChuyenTau chuyen = chuyenTauDAO.findById(maChuyen);
            chuyenChon[0] = chuyen;
            
            if (chuyen == null) return;
            
            // Load seats
            pnlGhe.removeAll();
            List<ToaTau> danhSachToa = toaTauDAO.getByTau(chuyen.getMaTau());
            for (ToaTau toa : danhSachToa) {
                List<Ghe> danhSachGhe = gheDAO.getByToa(toa.getMaToa());
                for (Ghe ghe : danhSachGhe) {
                    if ("Trống".equals(ghe.getTrangThai())) {
                        JButton btnGhe = new JButton(ghe.getMaGhe());
                        btnGhe.setBackground(new Color(34, 139, 34));
                        btnGhe.setForeground(Color.WHITE);
                        btnGhe.addActionListener(evt -> {
                            gheChon[0] = ghe;
                            btnGhe.setBackground(Color.ORANGE);
                        });
                        pnlGhe.add(btnGhe);
                    }
                }
            }
            pnlGhe.revalidate();
            pnlGhe.repaint();
        });
        
        dialog.add(pnlNoiDung, BorderLayout.CENTER);
        
        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnXacNhan = new JButton("Xác nhận đổi vé");
        btnXacNhan.addActionListener(e -> {
            if (gheChon[0] == null || chuyenChon[0] == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn ghế mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                // Create new ticket info
                Ve veMoi = new Ve();
                veMoi.setMaChuyen(chuyenChon[0].getMaChuyen());
                veMoi.setMaSoGhe(gheChon[0].getMaGhe());
                veMoi.setGaDi(chuyenChon[0].getGaDi());
                veMoi.setGaDen(chuyenChon[0].getGaDen());
                veMoi.setGioDi(chuyenChon[0].getGioDi());
                
                // Exchange ticket
                veService.doiVe(veHienTai.getMaVe(), veMoi);
                
                JOptionPane.showMessageDialog(dialog, "Đổi vé thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Refresh ticket list
                timKiemVe();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi đổi vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());
        
        pnlButton.add(btnXacNhan);
        pnlButton.add(btnHuy);
        dialog.add(pnlButton, BorderLayout.SOUTH);
        
        // Trigger initial load
        if (cmbChuyen.getItemCount() > 0) {
            cmbChuyen.setSelectedIndex(0);
        }
        
        dialog.setVisible(true);
    }
}
