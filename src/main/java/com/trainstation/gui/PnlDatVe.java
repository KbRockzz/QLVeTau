package com.trainstation.gui;

import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel đặt vé
 */
public class PnlDatVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private ChuyenTauDAO chuyenTauDAO;
    private ToaTauDAO toaTauDAO;
    private GheDAO gheDAO;
    private KhachHangDAO khachHangDAO;
    
    private JComboBox<String> cmbChuyenTau;
    private JTable bangToaTau;
    private DefaultTableModel modelBangToa;
    private JPanel pnlSoDoGhe;
    private ChuyenTau chuyenDuocChon;
    private ToaTau toaDuocChon;
    private Ghe gheDuocChon;

    public PnlDatVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.khachHangDAO = KhachHangDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("ĐẶT VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Main content panel
        JPanel pnlNoiDung = new JPanel(new BorderLayout(10, 10));
        
        // Left panel - Train and Carriage selection
        JPanel pnlTrai = new JPanel(new BorderLayout(5, 5));
        pnlTrai.setPreferredSize(new Dimension(400, 0));
        
        // Train selection
        JPanel pnlChonChuyen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlChonChuyen.add(new JLabel("Chọn chuyến tàu:"));
        cmbChuyenTau = new JComboBox<>();
        cmbChuyenTau.setPreferredSize(new Dimension(250, 25));
        cmbChuyenTau.addActionListener(e -> chonChuyenTau());
        pnlChonChuyen.add(cmbChuyenTau);
        pnlTrai.add(pnlChonChuyen, BorderLayout.NORTH);
        
        // Carriage table
        String[] tenCot = {"Mã toa", "Tên toa", "Loại toa", "Sức chứa"};
        modelBangToa = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangToaTau = new JTable(modelBangToa);
        bangToaTau.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chonToaTau();
            }
        });
        JScrollPane scrollToa = new JScrollPane(bangToaTau);
        scrollToa.setBorder(BorderFactory.createTitledBorder("Danh sách toa tàu"));
        pnlTrai.add(scrollToa, BorderLayout.CENTER);
        
        pnlNoiDung.add(pnlTrai, BorderLayout.WEST);
        
        // Right panel - Seat map
        pnlSoDoGhe = new JPanel(new GridLayout(0, 5, 5, 5));
        JScrollPane scrollGhe = new JScrollPane(pnlSoDoGhe);
        scrollGhe.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế"));
        pnlNoiDung.add(scrollGhe, BorderLayout.CENTER);
        
        add(pnlNoiDung, BorderLayout.CENTER);
        
        // Legend panel
        JPanel pnlChuThich = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTrong = new JLabel("■ Trống");
        lblTrong.setForeground(new Color(34, 139, 34));
        JLabel lblDaDat = new JLabel("■ Đã đặt");
        lblDaDat.setForeground(Color.RED);
        pnlChuThich.add(lblTrong);
        pnlChuThich.add(Box.createHorizontalStrut(20));
        pnlChuThich.add(lblDaDat);
        add(pnlChuThich, BorderLayout.SOUTH);
        
        // Load train data
        taiDanhSachChuyenTau();
    }

    private void taiDanhSachChuyenTau() {
        cmbChuyenTau.removeAllItems();
        List<ChuyenTau> danhSach = chuyenTauDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        for (ChuyenTau ct : danhSach) {
            String item = ct.getMaChuyen() + " - " + ct.getGaDi() + " → " + ct.getGaDen();
            if (ct.getGioDi() != null) {
                item += " (" + ct.getGioDi().format(formatter) + ")";
            }
            cmbChuyenTau.addItem(item);
        }
    }

    private void chonChuyenTau() {
        String selected = (String) cmbChuyenTau.getSelectedItem();
        if (selected == null) return;
        
        String maChuyen = selected.split(" - ")[0];
        chuyenDuocChon = chuyenTauDAO.findById(maChuyen);
        
        if (chuyenDuocChon == null) return;
        
        // Load carriages for selected train
        modelBangToa.setRowCount(0);
        List<ToaTau> danhSachToa = toaTauDAO.getByTau(chuyenDuocChon.getMaTau());
        for (ToaTau toa : danhSachToa) {
            modelBangToa.addRow(new Object[]{
                toa.getMaToa(),
                toa.getTenToa(),
                toa.getLoaiToa(),
                toa.getSucChua()
            });
        }
        
        pnlSoDoGhe.removeAll();
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    private void chonToaTau() {
        int row = bangToaTau.getSelectedRow();
        if (row < 0) return;
        
        String maToa = (String) modelBangToa.getValueAt(row, 0);
        toaDuocChon = toaTauDAO.findById(maToa);
        
        if (toaDuocChon == null) return;
        
        // Load seats for selected carriage
        hienThiSoDoGhe(maToa);
    }

    private void hienThiSoDoGhe(String maToa) {
        pnlSoDoGhe.removeAll();
        List<Ghe> danhSachGhe = gheDAO.getByToa(maToa);
        
        for (Ghe ghe : danhSachGhe) {
            JButton btnGhe = new JButton(ghe.getMaGhe());
            btnGhe.setPreferredSize(new Dimension(80, 40));
            
            // Set color based on status
            if ("Trống".equals(ghe.getTrangThai())) {
                btnGhe.setBackground(new Color(34, 139, 34)); // Green
                btnGhe.setForeground(Color.WHITE);
                btnGhe.setEnabled(true);
                btnGhe.addActionListener(e -> chonGhe(ghe));
            } else {
                btnGhe.setBackground(Color.RED);
                btnGhe.setForeground(Color.WHITE);
                btnGhe.setEnabled(false);
            }
            
            pnlSoDoGhe.add(btnGhe);
        }
        
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }

    private void chonGhe(Ghe ghe) {
        gheDuocChon = ghe;
        
        // Show customer info dialog
        hienThiFormKhachHang();
    }

    private void hienThiFormKhachHang() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thông tin khách hàng", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlForm = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtMaKH = new JTextField(20);
        JTextField txtTenKH = new JTextField(20);
        JTextField txtSDT = new JTextField(20);
        JTextField txtEmail = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã khách hàng:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtMaKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Tên khách hàng:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtTenKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtSDT, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtEmail, gbc);
        
        dialog.add(pnlForm, BorderLayout.CENTER);
        
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnXacNhan = new JButton("Xác nhận đặt vé");
        btnXacNhan.addActionListener(e -> {
            if (txtMaKH.getText().trim().isEmpty() || txtTenKH.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create or update customer
            KhachHang kh = khachHangDAO.findById(txtMaKH.getText().trim());
            if (kh == null) {
                kh = new KhachHang(txtMaKH.getText().trim(), txtTenKH.getText().trim(), 
                                   txtEmail.getText().trim(), txtSDT.getText().trim());
                khachHangDAO.insert(kh);
            }
            
            // Create ticket
            datVe(kh);
            dialog.dispose();
        });
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());
        
        pnlButton.add(btnXacNhan);
        pnlButton.add(btnHuy);
        dialog.add(pnlButton, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void datVe(KhachHang khachHang) {
        try {
            // Generate ticket ID
            String maVe = "VE" + System.currentTimeMillis();
            
            // Create ticket
            Ve ve = new Ve();
            ve.setMaVe(maVe);
            ve.setMaChuyen(chuyenDuocChon.getMaChuyen());
            ve.setMaSoGhe(gheDuocChon.getMaGhe());
            ve.setNgayIn(LocalDateTime.now());
            ve.setTrangThai("Đã đặt");
            ve.setGaDi(chuyenDuocChon.getGaDi());
            ve.setGaDen(chuyenDuocChon.getGaDen());
            ve.setGioDi(chuyenDuocChon.getGioDi());
            ve.setSoToa(toaDuocChon.getMaToa());
            ve.setLoaiCho(toaDuocChon.getLoaiToa());
            
            // Insert ticket
            veService.taoVe(ve);
            
            // Update seat status
            gheDuocChon.setTrangThai("Đã đặt");
            gheDAO.update(gheDuocChon);
            
            JOptionPane.showMessageDialog(this, "Đặt vé thành công!\nMã vé: " + maVe, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh seat map
            hienThiSoDoGhe(toaDuocChon.getMaToa());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đặt vé: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
