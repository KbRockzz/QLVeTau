package com.trainstation.gui;

import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel đổi vé - Phiên bản nâng cao với đầy đủ chức năng
 */
public class PnlDoiVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private DoiVeService doiVeService;
    private VeService veService;
    private ChuyenTauDAO chuyenTauDAO;
    private ToaTauDAO toaTauDAO;
    private GheDAO gheDAO;
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private VeDAO veDAO;
    private KhachHangDAO khachHangDAO;
    
    // Search fields
    private JTextField txtMaVe;
    private JTextField txtSoDienThoai;
    private JTextField txtCCCD;
    private ButtonGroup searchGroup;
    
    private JTable bangVe;
    private DefaultTableModel modelBangVe;
    private Ve veHienTai;
    private KhachHang khachHangHienTai;

    public PnlDoiVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.doiVeService = DoiVeService.getInstance();
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

        // Main content split: left (search & tickets) and right (ticket info)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);
        
        // Left panel: Search and ticket list
        JPanel pnlLeft = new JPanel(new BorderLayout(10, 10));
        
        // Search panel with multiple search options
        JPanel pnlTimKiem = new JPanel();
        pnlTimKiem.setLayout(new BoxLayout(pnlTimKiem, BoxLayout.Y_AXIS));
        pnlTimKiem.setBorder(BorderFactory.createTitledBorder("Tìm kiếm vé"));
        
        searchGroup = new ButtonGroup();
        
        // Search by ticket code
        JPanel pnlMaVe = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton rbMaVe = new JRadioButton("Mã vé:", true);
        searchGroup.add(rbMaVe);
        txtMaVe = new JTextField(20);
        pnlMaVe.add(rbMaVe);
        pnlMaVe.add(txtMaVe);
        pnlTimKiem.add(pnlMaVe);
        
        // Search by phone
        JPanel pnlSDT = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton rbSDT = new JRadioButton("Số điện thoại:");
        searchGroup.add(rbSDT);
        txtSoDienThoai = new JTextField(20);
        pnlSDT.add(rbSDT);
        pnlSDT.add(txtSoDienThoai);
        pnlTimKiem.add(pnlSDT);
        
        // Search by CCCD
        JPanel pnlCCCD = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton rbCCCD = new JRadioButton("CCCD/CMND:");
        searchGroup.add(rbCCCD);
        txtCCCD = new JTextField(20);
        pnlCCCD.add(rbCCCD);
        pnlCCCD.add(txtCCCD);
        pnlTimKiem.add(pnlCCCD);
        
        // Search button
        JPanel pnlBtnSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemVe());
        pnlBtnSearch.add(btnTimKiem);
        pnlTimKiem.add(pnlBtnSearch);
        
        pnlLeft.add(pnlTimKiem, BorderLayout.NORTH);
        
        // Ticket table
        String[] tenCot = {"Mã vé", "Chuyến", "Ga đi", "Ga đến", "Giờ đi", "Ghế", "Trạng thái", "Cho phép đổi"};
        modelBangVe = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangVe = new JTable(modelBangVe);
        bangVe.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                hienThiThongTinVe();
            }
        });
        JScrollPane scrollVe = new JScrollPane(bangVe);
        scrollVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));
        pnlLeft.add(scrollVe, BorderLayout.CENTER);
        
        // Button panel
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.addActionListener(e -> chonVeDeDoiVe());
        pnlButton.add(btnDoiVe);
        pnlLeft.add(pnlButton, BorderLayout.SOUTH);
        
        splitPane.setLeftComponent(pnlLeft);
        
        // Right panel: Ticket info and validation status (will be added dynamically)
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder("Thông tin vé đã chọn"));
        JTextArea txtInfo = new JTextArea("Chọn một vé để xem thông tin chi tiết");
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        pnlRight.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
        
        splitPane.setRightComponent(pnlRight);
        add(splitPane, BorderLayout.CENTER);
    }

    private void timKiemVe() {
        modelBangVe.setRowCount(0);
        khachHangHienTai = null;
        List<Ve> danhSachVe = new ArrayList<>();
        
        // Xác định phương thức tìm kiếm
        String maVe = txtMaVe.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String cccd = txtCCCD.getText().trim();
        
        if (!maVe.isEmpty()) {
            // Tìm theo mã vé
            Ve ve = veDAO.findById(maVe);
            if (ve != null) {
                danhSachVe.add(ve);
                // Tìm khách hàng từ vé
                timKhachHangTuVe(ve);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy vé với mã: " + maVe, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        } else if (!soDienThoai.isEmpty()) {
            // Tìm khách hàng theo số điện thoại
            List<KhachHang> khachHangs = khachHangDAO.getAll().stream()
                .filter(kh -> soDienThoai.equals(kh.getSoDienThoai()))
                .toList();
            
            if (khachHangs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với SĐT: " + soDienThoai, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            khachHangHienTai = khachHangs.get(0);
            danhSachVe = layVeTheoKhachHang(khachHangHienTai.getMaKhachHang());
        } else if (!cccd.isEmpty()) {
            // Tìm khách hàng theo CCCD (giả sử có trong email hoặc mở rộng model)
            // Tạm thời tìm theo email chứa CCCD
            List<KhachHang> khachHangs = khachHangDAO.getAll().stream()
                .filter(kh -> kh.getEmail() != null && kh.getEmail().contains(cccd))
                .toList();
            
            if (khachHangs.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với CCCD: " + cccd, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            khachHangHienTai = khachHangs.get(0);
            danhSachVe = layVeTheoKhachHang(khachHangHienTai.getMaKhachHang());
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập thông tin tìm kiếm!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Hiển thị danh sách vé
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Ve ve : danhSachVe) {
            // Kiểm tra vé có được phép đổi không
            DoiVeService.KetQuaKiemTra kiemTra = doiVeService.kiemTraChoPhepDoiVe(ve);
            String choPhepDoi = kiemTra.isHopLe() ? "✓ Được phép" : "✗ " + kiemTra.getThongBao();
            
            // Màu sắc cho trạng thái
            modelBangVe.addRow(new Object[]{
                ve.getMaVe(),
                ve.getMaChuyen(),
                ve.getGaDi(),
                ve.getGaDen(),
                ve.getGioDi() != null ? ve.getGioDi().format(formatter) : "",
                ve.getMaSoGhe(),
                ve.getTrangThai(),
                choPhepDoi
            });
        }
        
        if (danhSachVe.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé nào!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private List<Ve> layVeTheoKhachHang(String maKH) {
        List<Ve> danhSachVe = new ArrayList<>();
        List<HoaDon> danhSachHoaDon = hoaDonDAO.findByKhachHang(maKH);
        
        for (HoaDon hoaDon : danhSachHoaDon) {
            List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hoaDon.getMaHoaDon());
            for (ChiTietHoaDon chiTiet : chiTietList) {
                Ve ve = veDAO.findById(chiTiet.getMaVe());
                if (ve != null) {
                    danhSachVe.add(ve);
                }
            }
        }
        
        return danhSachVe;
    }
    
    private void timKhachHangTuVe(Ve ve) {
        // Tìm khách hàng từ hóa đơn chứa vé này
        List<HoaDon> allHoaDon = hoaDonDAO.getAll();
        for (HoaDon hoaDon : allHoaDon) {
            List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hoaDon.getMaHoaDon());
            for (ChiTietHoaDon chiTiet : chiTietList) {
                if (ve.getMaVe().equals(chiTiet.getMaVe())) {
                    khachHangHienTai = khachHangDAO.findById(hoaDon.getMaKH());
                    return;
                }
            }
        }
    }
    
    private void hienThiThongTinVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) return;
        
        String maVe = (String) modelBangVe.getValueAt(row, 0);
        veHienTai = veDAO.findById(maVe);
        
        if (veHienTai == null) return;
        
        // Cập nhật panel bên phải với thông tin chi tiết
        JPanel pnlRight = new JPanel();
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createTitledBorder("Thông tin vé đã chọn"));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        // Thông tin vé
        JTextArea txtInfo = new JTextArea();
        txtInfo.setEditable(false);
        txtInfo.setLineWrap(true);
        txtInfo.setWrapStyleWord(true);
        
        StringBuilder info = new StringBuilder();
        info.append("=== THÔNG TIN VÉ ===\n");
        info.append(String.format("Mã vé: %s\n", veHienTai.getMaVe()));
        info.append(String.format("Chuyến: %s\n", veHienTai.getMaChuyen()));
        info.append(String.format("Ga đi: %s\n", veHienTai.getGaDi()));
        info.append(String.format("Ga đến: %s\n", veHienTai.getGaDen()));
        info.append(String.format("Giờ đi: %s\n", veHienTai.getGioDi() != null ? veHienTai.getGioDi().format(formatter) : "N/A"));
        info.append(String.format("Ghế: %s\n", veHienTai.getMaSoGhe()));
        info.append(String.format("Toa: %s\n", veHienTai.getSoToa()));
        info.append(String.format("Loại chỗ: %s\n", veHienTai.getLoaiCho()));
        info.append(String.format("Trạng thái: %s\n\n", veHienTai.getTrangThai()));
        
        // Thông tin khách hàng
        if (khachHangHienTai != null) {
            info.append("=== THÔNG TIN KHÁCH HÀNG ===\n");
            info.append(String.format("Mã KH: %s\n", khachHangHienTai.getMaKhachHang()));
            info.append(String.format("Tên: %s\n", khachHangHienTai.getTenKhachHang()));
            info.append(String.format("SĐT: %s\n", khachHangHienTai.getSoDienThoai()));
            info.append(String.format("Email: %s\n\n", khachHangHienTai.getEmail()));
        }
        
        // Kiểm tra điều kiện đổi vé
        DoiVeService.KetQuaKiemTra kiemTra = doiVeService.kiemTraChoPhepDoiVe(veHienTai);
        info.append("=== KIỂM TRA ĐIỀU KIỆN ĐỔI ===\n");
        if (kiemTra.isHopLe()) {
            info.append("✓ Vé được phép đổi\n");
        } else {
            info.append("✗ " + kiemTra.getThongBao() + "\n");
        }
        
        txtInfo.setText(info.toString());
        pnlRight.add(new JScrollPane(txtInfo));
        
        // Replace right panel
        Component parent = getComponent(0); // BorderLayout.CENTER (splitPane)
        if (parent instanceof JSplitPane) {
            ((JSplitPane) parent).setRightComponent(pnlRight);
        }
    }

    private void chonVeDeDoiVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần đổi!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maVe = (String) modelBangVe.getValueAt(row, 0);
        veHienTai = veDAO.findById(maVe);
        
        if (veHienTai == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra lại điều kiện đổi vé
        DoiVeService.KetQuaKiemTra kiemTra = doiVeService.kiemTraChoPhepDoiVe(veHienTai);
        if (!kiemTra.isHopLe()) {
            JOptionPane.showMessageDialog(this, kiemTra.getThongBao(), "Không thể đổi vé", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show dialog to select new train/seat
        hienThiDialogChonChuyenMoi();
    }

    private void hienThiDialogChonChuyenMoi() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Đổi vé - Chọn chuyến và ghế mới", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);
        
        // Main split: left (selection) and right (financial summary)
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setDividerLocation(650);
        
        JPanel pnlNoiDung = new JPanel(new BorderLayout(10, 10));
        pnlNoiDung.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Tabbed pane for different exchange types
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Tab 1: Đổi chuyến tàu
        JPanel tabDoiChuyen = new JPanel(new BorderLayout(10, 10));
        
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
        tabDoiChuyen.add(pnlChonChuyen, BorderLayout.NORTH);
        
        // Seat selection panel
        JPanel pnlGhe = new JPanel(new GridLayout(0, 6, 5, 5));
        JScrollPane scrollGhe = new JScrollPane(pnlGhe);
        scrollGhe.setBorder(BorderFactory.createTitledBorder("Chọn ghế mới (Xanh: Trống, Cam: Đã chọn)"));
        tabDoiChuyen.add(scrollGhe, BorderLayout.CENTER);
        
        tabbedPane.addTab("Đổi chuyến tàu", tabDoiChuyen);
        
        // Tab 2: Cập nhật thông tin hành khách (placeholder)
        JPanel tabCapNhat = new JPanel(new BorderLayout());
        JTextArea txtNote = new JTextArea("Chức năng cập nhật thông tin hành khách\n(Tên, CCCD, SĐT, Email)\n\nĐang phát triển...");
        txtNote.setEditable(false);
        tabCapNhat.add(new JScrollPane(txtNote), BorderLayout.CENTER);
        tabbedPane.addTab("Cập nhật thông tin", tabCapNhat);
        
        pnlNoiDung.add(tabbedPane, BorderLayout.CENTER);
        mainSplit.setLeftComponent(pnlNoiDung);
        
        // Right panel: Financial summary
        JPanel pnlTaiChinh = new JPanel(new BorderLayout());
        pnlTaiChinh.setBorder(BorderFactory.createTitledBorder("Tóm tắt tài chính"));
        
        JTextArea txtTaiChinh = new JTextArea();
        txtTaiChinh.setEditable(false);
        txtTaiChinh.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtTaiChinh.setText("Chọn chuyến và ghế để xem chi phí");
        
        JScrollPane scrollTC = new JScrollPane(txtTaiChinh);
        pnlTaiChinh.add(scrollTC, BorderLayout.CENTER);
        
        // Note field
        JPanel pnlGhiChu = new JPanel(new BorderLayout());
        pnlGhiChu.add(new JLabel("Ghi chú:"), BorderLayout.NORTH);
        JTextArea txtGhiChu = new JTextArea(3, 20);
        txtGhiChu.setLineWrap(true);
        pnlGhiChu.add(new JScrollPane(txtGhiChu), BorderLayout.CENTER);
        pnlTaiChinh.add(pnlGhiChu, BorderLayout.SOUTH);
        
        mainSplit.setRightComponent(pnlTaiChinh);
        dialog.add(mainSplit, BorderLayout.CENTER);
        
        final Ghe[] gheChon = {null};
        final ChuyenTau[] chuyenChon = {null};
        final JButton[] currentSelectedBtn = {null};
        
        cmbChuyen.addActionListener(e -> {
            String selected = (String) cmbChuyen.getSelectedItem();
            if (selected == null) return;
            
            String maChuyen = selected.split(" - ")[0];
            ChuyenTau chuyen = chuyenTauDAO.findById(maChuyen);
            chuyenChon[0] = chuyen;
            gheChon[0] = null;
            currentSelectedBtn[0] = null;
            
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
                            // Reset previous selection
                            if (currentSelectedBtn[0] != null) {
                                currentSelectedBtn[0].setBackground(new Color(34, 139, 34));
                            }
                            
                            gheChon[0] = ghe;
                            btnGhe.setBackground(Color.ORANGE);
                            currentSelectedBtn[0] = btnGhe;
                            
                            // Update financial summary
                            capNhatTomTatTaiChinh(txtTaiChinh, chuyenChon[0], ghe);
                        });
                        pnlGhe.add(btnGhe);
                    }
                }
            }
            pnlGhe.revalidate();
            pnlGhe.repaint();
            
            // Reset financial summary
            txtTaiChinh.setText("Chọn ghế để xem chi phí đổi vé");
        });
        
        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnXacNhan = new JButton("Xác nhận đổi vé");
        btnXacNhan.addActionListener(e -> {
            if (gheChon[0] == null || chuyenChon[0] == null) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn chuyến và ghế mới!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
                veMoi.setMaLoaiVe(veHienTai.getMaLoaiVe());
                veMoi.setLoaiCho(gheChon[0].getLoaiGhe());
                
                // Find seat's toa
                ToaTau toa = toaTauDAO.findById(gheChon[0].getMaToa());
                if (toa != null) {
                    veMoi.setSoToa(toa.getMaToa());
                }
                
                // Exchange ticket using new service
                String maNV = taiKhoanHienTai != null && taiKhoanHienTai.getMaNV() != null ? 
                             taiKhoanHienTai.getMaNV() : "SYSTEM";
                String maKH = khachHangHienTai != null ? khachHangHienTai.getMaKhachHang() : null;
                String ghiChu = txtGhiChu.getText().trim();
                
                DoiVeService.KetQuaDoiVe ketQua = doiVeService.doiVe(veHienTai.getMaVe(), veMoi, maNV, maKH, ghiChu);
                
                if (ketQua.isThanhCong()) {
                    StringBuilder msg = new StringBuilder("Đổi vé thành công!\n\n");
                    msg.append("Mã giao dịch: ").append(ketQua.getMaGiaoDich()).append("\n");
                    
                    if (ketQua.getThongTinPhi() != null) {
                        DoiVeService.ThongTinPhiDoiVe phi = ketQua.getThongTinPhi();
                        msg.append(String.format("Phí đổi vé: %,.0f VNĐ\n", phi.getPhiDoiVe()));
                        if (phi.getSoTienThu() > 0) {
                            msg.append(String.format("Số tiền cần thu: %,.0f VNĐ\n", phi.getSoTienThu()));
                        } else if (phi.getSoTienHoan() > 0) {
                            msg.append(String.format("Số tiền hoàn lại: %,.0f VNĐ\n", phi.getSoTienHoan()));
                        }
                    }
                    
                    if (ketQua.isCanPheDuyet()) {
                        msg.append("\n⚠ Giao dịch cần phê duyệt từ quản lý");
                    }
                    
                    JOptionPane.showMessageDialog(dialog, msg.toString(), "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    
                    // Refresh ticket list
                    timKiemVe();
                } else {
                    JOptionPane.showMessageDialog(dialog, ketQua.getThongBao(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi đổi vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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
    
    private void capNhatTomTatTaiChinh(JTextArea txtTaiChinh, ChuyenTau chuyenMoi, Ghe gheMoi) {
        try {
            // Create temporary new ticket for calculation
            Ve veMoi = new Ve();
            veMoi.setMaChuyen(chuyenMoi.getMaChuyen());
            veMoi.setMaSoGhe(gheMoi.getMaGhe());
            veMoi.setGaDi(chuyenMoi.getGaDi());
            veMoi.setGaDen(chuyenMoi.getGaDen());
            veMoi.setGioDi(chuyenMoi.getGioDi());
            veMoi.setMaLoaiVe(veHienTai.getMaLoaiVe());
            veMoi.setLoaiCho(gheMoi.getLoaiGhe());
            
            // Calculate fees
            DoiVeService.ThongTinPhiDoiVe phi = doiVeService.tinhPhiDoiVe(veHienTai, veMoi);
            
            StringBuilder summary = new StringBuilder();
            summary.append("═══ TÓM TẮT TÀI CHÍNH ═══\n\n");
            summary.append(String.format("Giá vé cũ:        %,15.0f VNĐ\n", phi.getGiaVeCu()));
            summary.append(String.format("Giá vé mới:       %,15.0f VNĐ\n", phi.getGiaVeMoi()));
            summary.append("─────────────────────────────────────\n");
            summary.append(String.format("Chênh lệch:       %,15.0f VNĐ\n", phi.getChenhLechGia()));
            summary.append("\n");
            summary.append(String.format("Phí đổi vé (%s):\n", phi.getMucPhi()));
            summary.append(String.format("                  %,15.0f VNĐ\n", phi.getPhiDoiVe()));
            summary.append("═════════════════════════════════════\n");
            
            if (phi.getSoTienThu() > 0) {
                summary.append(String.format("CẦN THU THÊM:     %,15.0f VNĐ\n", phi.getSoTienThu()));
            } else if (phi.getSoTienHoan() > 0) {
                summary.append(String.format("SỐ TIỀN HOÀN:     %,15.0f VNĐ\n", phi.getSoTienHoan()));
            } else {
                summary.append("                  KHÔNG PHÍ PHÁT SINH\n");
            }
            
            txtTaiChinh.setText(summary.toString());
        } catch (Exception ex) {
            txtTaiChinh.setText("Không thể tính phí: " + ex.getMessage());
        }
    }
}
