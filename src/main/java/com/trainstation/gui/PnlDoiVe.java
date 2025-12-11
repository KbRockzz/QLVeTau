package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel đổi vé - Enhanced version with better UX
 */
public class PnlDoiVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private ChuyenTauDAO chuyenTauDAO;
    private ToaTauDAO toaTauDAO;
    private GheDAO gheDAO;
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private VeDAO veDAO;
    private KhachHangDAO khachHangDAO;
    
    private JTextField txtMaKH;
    private JTextField txtSDT;
    private JButton btnTimKiem;
    private JTable bangVe;
    private DefaultTableModel modelBangVe;
    private Ve veHienTai;
    private JComboBox<String> cmbLocTrangThai;

    public PnlDoiVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.veDAO = VeDAO.getInstance();
        this.khachHangDAO = KhachHangDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("ĐỔI VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Search panel with multiple search options
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        pnlTimKiem.add(new JLabel("Mã KH:"));
        txtMaKH = new JTextField(15);
        pnlTimKiem.add(txtMaKH);
        
        pnlTimKiem.add(new JLabel("Hoặc SĐT:"));
        txtSDT = new JTextField(15);
        pnlTimKiem.add(txtSDT);
        
        pnlTimKiem.add(new JLabel("Lọc trạng thái:"));
        cmbLocTrangThai = new JComboBox<>(new String[]{
            "Tất cả", "Đã thanh toán", "Đã đặt"
        });
        pnlTimKiem.add(cmbLocTrangThai);
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemVe());
        MaterialInitializer.styleButton(btnTimKiem);
        pnlTimKiem.add(btnTimKiem);
        
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        
        // Ticket table with enhanced columns
        String[] tenCot = {"Mã vé", "Chuyến", "Ga đi", "Ga đến", "Giờ đi", "Toa", "Ghế", "Loại vé", "Trạng thái"};
        modelBangVe = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangVe = new JTable(modelBangVe);
        bangVe.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollVe = new JScrollPane(bangVe);
        scrollVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé (Chỉ vé Đã thanh toán/Đã đặt mới có thể đổi)"));
        MaterialInitializer.setTableScrollPaneSize(scrollVe, 40);
        pnlTop.add(scrollVe, BorderLayout.CENTER);
        
        add(pnlTop, BorderLayout.CENTER);
        
        // Button panel
        JPanel pnlButton = MaterialInitializer.createButtonPanel();
        JButton btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.addActionListener(e -> chonVeDeDoiVe());
        MaterialInitializer.styleButton(btnDoiVe);
        pnlButton.add(btnDoiVe);
        
        add(pnlButton, BorderLayout.SOUTH);
    }

    private void timKiemVe() {
        String maKH = txtMaKH.getText().trim();
        String sdt = txtSDT.getText().trim();
        
        if (maKH.isEmpty() && sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập mã khách hàng hoặc số điện thoại!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        modelBangVe.setRowCount(0);
        List<Ve> danhSachVe = new ArrayList<>();
        
        // Search by customer ID or phone
        List<KhachHang> khachHangList = new ArrayList<>();
        if (!maKH.isEmpty()) {
            KhachHang kh = khachHangDAO.findById(maKH);
            if (kh != null) {
                khachHangList.add(kh);
            }
        } else if (!sdt.isEmpty()) {
            // Search by phone
            khachHangList = khachHangDAO.getAll().stream()
                .filter(kh -> sdt.equals(kh.getSoDienThoai()))
                .toList();
        }
        
        if (khachHangList.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khách hàng!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Get all tickets for found customers
        for (KhachHang kh : khachHangList) {
            List<HoaDon> danhSachHoaDon = hoaDonDAO.findByKhachHang(kh.getMaKhachHang());
            
            for (HoaDon hoaDon : danhSachHoaDon) {
                List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hoaDon.getMaHoaDon());
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    Ve ve = veDAO.findById(chiTiet.getMaVe());
                    if (ve != null) {
                        danhSachVe.add(ve);
                    }
                }
            }
        }
        
        // Apply status filter
        String locTrangThai = (String) cmbLocTrangThai.getSelectedItem();
        if (!"Tất cả".equals(locTrangThai)) {
            danhSachVe = danhSachVe.stream()
                .filter(ve -> locTrangThai.equals(ve.getTrangThai()))
                .toList();
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Ve ve : danhSachVe) {
            // Highlight exchangeable tickets
            boolean coTheDoiVe = "Đã thanh toán".equals(ve.getTrangThai()) || 
                                 "Đã đặt".equals(ve.getTrangThai());
            
            modelBangVe.addRow(new Object[]{
                ve.getMaVe(),
                ve.getMaChuyen(),
                ve.getGaDi(),
                ve.getGaDen(),
                ve.getGioDi() != null ? ve.getGioDi().format(formatter) : "",
                ve.getSoToa(),
                ve.getMaSoGhe(),
                ve.getLoaiVe(),
                ve.getTrangThai() + (coTheDoiVe ? " ✓" : "")
            });
        }
        
        if (modelBangVe.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy vé nào!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void chonVeDeDoiVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn vé cần đổi!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maVe = (String) modelBangVe.getValueAt(row, 0);
        veHienTai = veService.timVeTheoMa(maVe);
        
        if (veHienTai == null) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy vé!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate ticket can be exchanged
        if (!"Đã thanh toán".equals(veHienTai.getTrangThai()) && 
            !"Đã đặt".equals(veHienTai.getTrangThai())) {
            JOptionPane.showMessageDialog(this, 
                "Chỉ có thể đổi vé ở trạng thái 'Đã thanh toán' hoặc 'Đã đặt'!\nTrạng thái hiện tại: " + veHienTai.getTrangThai(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show dialog to select new train/seat
        hienThiDialogChonChuyenMoi();
    }

    private void hienThiDialogChonChuyenMoi() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đổi vé: " + veHienTai.getMaVe(), true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 10));
        pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // LEFT PANEL: Old ticket info (read-only)
        JPanel pnlVeCu = new JPanel(new BorderLayout());
        pnlVeCu.setBorder(BorderFactory.createTitledBorder("Thông tin vé cũ"));
        
        JPanel pnlThongTinCu = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlThongTinCu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        pnlThongTinCu.add(new JLabel("Mã vé:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getMaVe()));
        pnlThongTinCu.add(new JLabel("Chuyến tàu:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getMaChuyen()));
        pnlThongTinCu.add(new JLabel("Ga đi:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getGaDi()));
        pnlThongTinCu.add(new JLabel("Ga đến:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getGaDen()));
        pnlThongTinCu.add(new JLabel("Giờ đi:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getGioDi() != null ? veHienTai.getGioDi().format(formatter) : "N/A"));
        pnlThongTinCu.add(new JLabel("Toa:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getSoToa()));
        pnlThongTinCu.add(new JLabel("Ghế:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getMaSoGhe()));
        pnlThongTinCu.add(new JLabel("Loại chỗ:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getLoaiCho()));
        pnlThongTinCu.add(new JLabel("Loại vé:"));
        pnlThongTinCu.add(new JLabel(veHienTai.getLoaiVe()));
        
        pnlVeCu.add(pnlThongTinCu, BorderLayout.CENTER);
        
        // RIGHT PANEL: New train/seat selection
        JPanel pnlVeMoi = new JPanel(new BorderLayout(5, 5));
        pnlVeMoi.setBorder(BorderFactory.createTitledBorder("Chọn chuyến và ghế mới"));
        
        // Train selection
        JPanel pnlChonChuyen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlChonChuyen.add(new JLabel("Chuyến mới:"));
        JComboBox<String> cmbChuyen = new JComboBox<>();
        List<ChuyenTau> danhSachChuyen = chuyenTauDAO.getAll();
        for (ChuyenTau ct : danhSachChuyen) {
            String item = ct.getMaChuyen() + " - " + ct.getGaDi() + " → " + ct.getGaDen();
            if (ct.getGioDi() != null) {
                item += " (" + ct.getGioDi().format(formatter) + ")";
            }
            cmbChuyen.addItem(item);
        }
        cmbChuyen.setPreferredSize(new Dimension(400, 25));
        pnlChonChuyen.add(cmbChuyen);
        pnlVeMoi.add(pnlChonChuyen, BorderLayout.NORTH);
        
        // Seat selection panel
        JPanel pnlGhe = new JPanel(new GridLayout(0, 6, 5, 5));
        JScrollPane scrollGhe = new JScrollPane(pnlGhe);
        scrollGhe.setBorder(BorderFactory.createTitledBorder("Chọn ghế trống"));
        pnlVeMoi.add(scrollGhe, BorderLayout.CENTER);
        
        final Ghe[] gheChon = {null};
        final ChuyenTau[] chuyenChon = {null};
        final ToaTau[] toaChon = {null};
        
        cmbChuyen.addActionListener(e -> {
            String selected = (String) cmbChuyen.getSelectedItem();
            if (selected == null) return;
            
            String maChuyen = selected.split(" - ")[0];
            ChuyenTau chuyen = chuyenTauDAO.findById(maChuyen);
            chuyenChon[0] = chuyen;
            
            if (chuyen == null) return;
            
            // Load seats grouped by toa
            pnlGhe.removeAll();
            List<ToaTau> danhSachToa = toaTauDAO.getByTau(chuyen.getMaTau());
            
            for (ToaTau toa : danhSachToa) {
                // Add toa header
                JLabel lblToa = new JLabel("Toa " + toa.getTenToa(), SwingConstants.CENTER);
                lblToa.setFont(new Font("Arial", Font.BOLD, 12));
                lblToa.setOpaque(true);
                lblToa.setBackground(new Color(200, 200, 200));
                pnlGhe.add(lblToa);
                
                List<Ghe> danhSachGhe = gheDAO.getByToa(toa.getMaToa());
                for (Ghe ghe : danhSachGhe) {
                    JButton btnGhe = new JButton(ghe.getMaGhe());
                    
                    if ("Trống".equals(ghe.getTrangThai())) {
                        btnGhe.setBackground(new Color(34, 139, 34)); // Green
                        btnGhe.setForeground(Color.WHITE);
                        btnGhe.setEnabled(true);
                        btnGhe.addActionListener(evt -> {
                            // Reset previous selection
                            for (Component comp : pnlGhe.getComponents()) {
                                if (comp instanceof JButton && ((JButton) comp).getBackground().equals(Color.ORANGE)) {
                                    ((JButton) comp).setBackground(new Color(34, 139, 34));
                                }
                            }
                            gheChon[0] = ghe;
                            toaChon[0] = toa;
                            btnGhe.setBackground(Color.ORANGE);
                        });
                    } else {
                        btnGhe.setBackground(Color.GRAY);
                        btnGhe.setForeground(Color.WHITE);
                        btnGhe.setEnabled(false);
                    }
                    
                    pnlGhe.add(btnGhe);
                }
            }
            pnlGhe.revalidate();
            pnlGhe.repaint();
        });
        
        pnlMain.add(pnlVeCu);
        pnlMain.add(pnlVeMoi);
        
        dialog.add(pnlMain, BorderLayout.CENTER);
        
        // Bottom panel: Reason and buttons
        JPanel pnlBottom = new JPanel(new BorderLayout(10, 10));
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pnlLyDo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLyDo.add(new JLabel("Lý do đổi vé:"));
        JTextField txtLyDo = new JTextField(40);
        pnlLyDo.add(txtLyDo);
        pnlBottom.add(pnlLyDo, BorderLayout.NORTH);
        
        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnXacNhan = new JButton("Xác nhận đổi vé");
        btnXacNhan.addActionListener(e -> {
            if (gheChon[0] == null || chuyenChon[0] == null || toaChon[0] == null) {
                JOptionPane.showMessageDialog(dialog, 
                    "Vui lòng chọn ghế mới!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String lyDo = txtLyDo.getText().trim();
            if (lyDo.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Vui lòng nhập lý do đổi vé!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                veMoi.setSoToa(toaChon[0].getTenToa());
                veMoi.setLoaiCho(toaChon[0].getLoaiToa());
                
                // Exchange ticket
                veService.doiVe(veHienTai.getMaVe(), veMoi);
                
                JOptionPane.showMessageDialog(dialog, 
                    "Đổi vé thành công!\nLý do: " + lyDo, 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                
                // Refresh ticket list
                timKiemVe();
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(dialog, 
                    "Lỗi khi đổi vé: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        MaterialInitializer.styleButton(btnXacNhan);
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());
        MaterialInitializer.styleButton(btnHuy);
        
        pnlButton.add(btnXacNhan);
        pnlButton.add(btnHuy);
        pnlBottom.add(pnlButton, BorderLayout.CENTER);
        
        dialog.add(pnlBottom, BorderLayout.SOUTH);
        
        // Trigger initial load
        if (cmbChuyen.getItemCount() > 0) {
            cmbChuyen.setSelectedIndex(0);
        }
        
        dialog.setVisible(true);
    }
}
