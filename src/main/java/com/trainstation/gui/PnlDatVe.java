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
    private LoaiVeDAO loaiVeDAO;
    
    private JComboBox<String> cmbChuyenTau;
    private JComboBox<String> cboKhachHang;
    private JComboBox<String> cboLoaiVe;
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
        this.loaiVeDAO = LoaiVeDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel with title and selection controls
        JPanel pnlTop = new JPanel(new BorderLayout(5, 5));
        
        // Title
        JLabel lblTieuDe = new JLabel("ĐẶT VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        pnlTop.add(lblTieuDe, BorderLayout.NORTH);
        
        // Customer and ticket type selection panel
        JPanel pnlChonKhachHangVaLoaiVe = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Customer selection
        pnlChonKhachHangVaLoaiVe.add(new JLabel("Khách hàng:"));
        cboKhachHang = new JComboBox<>();
        cboKhachHang.setPreferredSize(new Dimension(200, 25));
        pnlChonKhachHangVaLoaiVe.add(cboKhachHang);
        
        JButton btnThemKhachHang = new JButton("+ Thêm khách hàng mới");
        btnThemKhachHang.addActionListener(e -> hienThiFormThemKhachHang());
        pnlChonKhachHangVaLoaiVe.add(btnThemKhachHang);
        
        // Ticket type selection
        pnlChonKhachHangVaLoaiVe.add(Box.createHorizontalStrut(20));
        pnlChonKhachHangVaLoaiVe.add(new JLabel("Loại vé:"));
        cboLoaiVe = new JComboBox<>();
        cboLoaiVe.setPreferredSize(new Dimension(150, 25));
        pnlChonKhachHangVaLoaiVe.add(cboLoaiVe);
        
        pnlTop.add(pnlChonKhachHangVaLoaiVe, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

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
        
        // Right panel - Seat map with train layout
        pnlSoDoGhe = new JPanel();
        JScrollPane scrollGhe = new JScrollPane(pnlSoDoGhe);
        scrollGhe.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế (Bố trí toa tàu)"));
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
        
        // Load data
        taiDanhSachChuyenTau();
        taiDanhSachKhachHang();
        taiDanhSachLoaiVe();
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
    
    private void taiDanhSachKhachHang() {
        cboKhachHang.removeAllItems();
        List<KhachHang> danhSach = khachHangDAO.getAll();
        for (KhachHang kh : danhSach) {
            String item = kh.getTenKhachHang() + " (" + kh.getSoDienThoai() + ")";
            cboKhachHang.addItem(item);
        }
    }
    
    private void taiDanhSachLoaiVe() {
        cboLoaiVe.removeAllItems();
        List<LoaiVe> danhSach = loaiVeDAO.getAll();
        for (LoaiVe lv : danhSach) {
            cboLoaiVe.addItem(lv.getTenLoai());
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
        
        if (danhSachGhe.isEmpty()) {
            pnlSoDoGhe.setLayout(new FlowLayout());
            pnlSoDoGhe.add(new JLabel("Không có ghế nào trong toa này"));
        } else {
            // Calculate number of rows (4 seats per row)
            int soGhe = danhSachGhe.size();
            int soHang = (int) Math.ceil(soGhe / 4.0);
            
            // Setup layout: 5 columns (2 left seats + aisle + 2 right seats)
            pnlSoDoGhe.setLayout(new GridLayout(soHang, 5, 5, 5));
            
            for (int i = 0; i < soHang; i++) {
                // Left side - 2 seats
                for (int j = 0; j < 2; j++) {
                    int index = i * 4 + j;
                    if (index < soGhe) {
                        pnlSoDoGhe.add(taoNutGhe(danhSachGhe.get(index)));
                    } else {
                        pnlSoDoGhe.add(new JLabel(""));
                    }
                }
                
                // Aisle (middle corridor)
                JPanel pnlLoiDi = new JPanel();
                pnlLoiDi.setBackground(new Color(200, 200, 200));
                pnlLoiDi.setPreferredSize(new Dimension(30, 40));
                pnlSoDoGhe.add(pnlLoiDi);
                
                // Right side - 2 seats
                for (int j = 2; j < 4; j++) {
                    int index = i * 4 + j;
                    if (index < soGhe) {
                        pnlSoDoGhe.add(taoNutGhe(danhSachGhe.get(index)));
                    } else {
                        pnlSoDoGhe.add(new JLabel(""));
                    }
                }
            }
        }
        
        pnlSoDoGhe.revalidate();
        pnlSoDoGhe.repaint();
    }
    
    private JButton taoNutGhe(Ghe ghe) {
        JButton btnGhe = new JButton(ghe.getMaGhe());
        btnGhe.setPreferredSize(new Dimension(80, 40));
        btnGhe.setFont(new Font("Arial", Font.BOLD, 12));
        btnGhe.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        // Set color and tooltip based on status
        if ("Rảnh".equals(ghe.getTrangThai())) {
            btnGhe.setBackground(new Color(34, 139, 34)); // Green
//            btnGhe.setForeground(Color.WHITE);
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(true);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Trống");
            btnGhe.addActionListener(e -> chonGhe(ghe));
        } else {
            btnGhe.setBackground(Color.RED);
//            btnGhe.setForeground(Color.WHITE);
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(false);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Đã đặt");
        }
        
        return btnGhe;
    }

    private void chonGhe(Ghe ghe) {
        gheDuocChon = ghe;
        
        // Validate selections
        if (cboKhachHang.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (cboLoaiVe.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show confirmation dialog
        xacNhanDatVe();
    }
    
    private void xacNhanDatVe() {
        String khachHangStr = (String) cboKhachHang.getSelectedItem();
        String loaiVeStr = (String) cboLoaiVe.getSelectedItem();
        
        // Extract customer phone from selection (format: "Name (phone)")
        String sdt = khachHangStr.substring(khachHangStr.lastIndexOf("(") + 1, khachHangStr.lastIndexOf(")"));
        
        // Find customer by phone
        KhachHang khachHang = null;
        List<KhachHang> danhSachKH = khachHangDAO.getAll();
        for (KhachHang kh : danhSachKH) {
            if (sdt.equals(kh.getSoDienThoai())) {
                khachHang = kh;
                break;
            }
        }
        
        if (khachHang == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Find ticket type
        LoaiVe loaiVe = null;
        List<LoaiVe> danhSachLoaiVe = loaiVeDAO.getAll();
        for (LoaiVe lv : danhSachLoaiVe) {
            if (loaiVeStr.equals(lv.getTenLoai())) {
                loaiVe = lv;
                break;
            }
        }
        
        if (loaiVe == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin loại vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm booking
        String message = String.format(
            "Xác nhận đặt vé:\n\n" +
            "Khách hàng: %s\n" +
            "Chuyến tàu: %s → %s\n" +
            "Toa: %s\n" +
            "Ghế: %s\n" +
            "Loại vé: %s\n\n" +
            "Bạn có chắc chắn muốn đặt vé này?",
            khachHang.getTenKhachHang(),
            chuyenDuocChon.getGaDi(), chuyenDuocChon.getGaDen(),
            toaDuocChon.getTenToa(),
            gheDuocChon.getMaGhe(),
            loaiVe.getTenLoai()
        );
        
        int choice = JOptionPane.showConfirmDialog(this, message, "Xác nhận đặt vé", 
                                                   JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            datVe(khachHang, loaiVe);
        }
    }
    
    private void hienThiFormThemKhachHang() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm khách hàng mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlForm = new JPanel(new GridBagLayout());
        pnlForm.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField txtMaKH = new JTextField(20);
        JTextField txtTenKH = new JTextField(20);
        JTextField txtSDT = new JTextField(20);
        JTextField txtCCCD = new JTextField(20);
        JTextField txtDiaChi = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        pnlForm.add(new JLabel("Mã khách hàng:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtMaKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        pnlForm.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtTenKH, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        pnlForm.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtSDT, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        pnlForm.add(new JLabel("CCCD:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtCCCD, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        pnlForm.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        pnlForm.add(txtDiaChi, gbc);
        
        dialog.add(pnlForm, BorderLayout.CENTER);
        
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnThem = new JButton("Thêm");
        btnThem.addActionListener(e -> {
            String maKH = txtMaKH.getText().trim();
            String tenKH = txtTenKH.getText().trim();
            String sdt = txtSDT.getText().trim();
            
            if (maKH.isEmpty() || tenKH.isEmpty() || sdt.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng nhập đầy đủ thông tin bắt buộc (Mã KH, Họ tên, SĐT)!", 
                                             "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if customer already exists
            if (khachHangDAO.findById(maKH) != null) {
                JOptionPane.showMessageDialog(dialog, "Mã khách hàng đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new customer (using email field to store CCCD and address)
            String email = txtCCCD.getText().trim() + "|" + txtDiaChi.getText().trim();
            KhachHang kh = new KhachHang(maKH, tenKH, email, sdt);
            
            if (khachHangDAO.insert(kh)) {
                JOptionPane.showMessageDialog(dialog, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                taiDanhSachKhachHang();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi thêm khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());
        
        pnlButton.add(btnThem);
        pnlButton.add(btnHuy);
        dialog.add(pnlButton, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }

    private void datVe(KhachHang khachHang, LoaiVe loaiVe) {
        try {
            // Generate ticket ID
            String maVe = "VE" + System.currentTimeMillis();
            
            // Create ticket
            Ve ve = new Ve();
            ve.setMaVe(maVe);
            ve.setMaChuyen(chuyenDuocChon.getMaChuyen());
            ve.setMaLoaiVe(loaiVe.getMaLoaiVe());
            ve.setMaSoGhe(gheDuocChon.getMaGhe());
            ve.setNgayIn(LocalDateTime.now());
            ve.setTrangThai("Đã đặt");
            ve.setGaDi(chuyenDuocChon.getGaDi());
            ve.setGaDen(chuyenDuocChon.getGaDen());
            ve.setGioDi(chuyenDuocChon.getGioDi());
            ve.setSoToa(toaDuocChon.getMaToa());
            ve.setLoaiCho(toaDuocChon.getLoaiToa());
            ve.setLoaiVe(loaiVe.getTenLoai());
            
            // Insert ticket
            veService.taoVe(ve);
            
            // Update seat status
            gheDuocChon.setTrangThai("Đã đặt");
            gheDAO.update(gheDuocChon);
            
            // Auto-create or find open invoice for customer
            HoaDonDAO hoaDonDAO = HoaDonDAO.getInstance();
            ChiTietHoaDonDAO chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
            BangGiaDAO bangGiaDAO = BangGiaDAO.getInstance();
            
            // Find open invoice for customer
            HoaDon hoaDonMo = null;
            List<HoaDon> danhSachHoaDon = hoaDonDAO.findByKhachHang(khachHang.getMaKhachHang());
            for (HoaDon hd : danhSachHoaDon) {
                if ("Chờ xác nhận".equals(hd.getTrangThai())) {
                    hoaDonMo = hd;
                    break;
                }
            }
            
            // If no open invoice, create new one
            if (hoaDonMo == null) {
                String maHoaDon = "HD" + System.currentTimeMillis();
                hoaDonMo = new HoaDon();
                hoaDonMo.setMaHoaDon(maHoaDon);
                hoaDonMo.setMaNV(taiKhoanHienTai != null ? taiKhoanHienTai.getMaNV() : "NV001");
                hoaDonMo.setMaKH(khachHang.getMaKhachHang());
                hoaDonMo.setNgayLap(null); // Will be set when confirmed
                hoaDonMo.setPhuongThucThanhToan(null); // Will be set when confirmed
                hoaDonMo.setTrangThai("Chờ xác nhận");
                hoaDonDAO.insert(hoaDonMo);
            }
            
            // Get price from BangGia or use default
            float giaVe = 100000; // Default price
            List<BangGia> bangGiaList = bangGiaDAO.getAll();
            if (!bangGiaList.isEmpty()) {
                giaVe = bangGiaList.get(0).getGiaCoBan();
            }
            
            // Add ticket to invoice details
            ChiTietHoaDon chiTiet = new ChiTietHoaDon();
            chiTiet.setMaHoaDon(hoaDonMo.getMaHoaDon());
            chiTiet.setMaVe(maVe);
            chiTiet.setMaLoaiVe(loaiVe.getMaLoaiVe());
            chiTiet.setGiaGoc(giaVe);
            chiTiet.setGiaDaKM(giaVe); // No discount for now
            chiTiet.setMoTa("Ve " + chuyenDuocChon.getGaDi() + " - " + chuyenDuocChon.getGaDen());
            chiTietHoaDonDAO.insert(chiTiet);
            
            String message = String.format(
                "Đặt vé thành công!\n\n" +
                "Mã vé: %s\n" +
                "Mã hóa đơn: %s\n" +
                "Khách hàng: %s\n" +
                "Loại vé: %s\n" +
                "Ghế: %s\n\n" +
                "Vé đã được thêm vào hóa đơn.",
                maVe,
                hoaDonMo.getMaHoaDon(),
                khachHang.getTenKhachHang(),
                loaiVe.getTenLoai(),
                gheDuocChon.getMaGhe()
            );
            
            JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh seat map
            hienThiSoDoGhe(toaDuocChon.getMaToa());
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đặt vé: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}
