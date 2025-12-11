package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import com.trainstation.dto.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.toedter.calendar.JDateChooser;

/**
 * Panel đổi vé - Enhanced version với tìm kiếm theo SĐT và giao diện đầy đủ
 */
public class PnlDoiVeEnhanced extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private ChuyenTauDAO chuyenTauDAO;
    private ToaTauDAO toaTauDAO;
    private GheDAO gheDAO;
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private KhachHangDAO khachHangDAO;
    private VeDAO veDAO;
    
    // UI Components
    private JTextField txtSoDienThoai;
    private JButton btnTimKiem;
    private JButton btnDoiVe;
    private JButton btnInVe;
    private JButton btnRefresh;
    private JTable tblVeKhachHang;
    private DefaultTableModel modelBangVe;
    private Ve veHienTai;
    private KhachHang khachHangHienTai;

    public PnlDoiVeEnhanced(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        initializeServices();
        initComponents();
    }

    private void initializeServices() {
        this.veService = VeService.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.khachHangDAO = KhachHangDAO.getInstance();
        this.veDAO = VeDAO.getInstance();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("ĐỔI VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Center panel with search and table
        JPanel pnlCenter = new JPanel(new BorderLayout(10, 10));
        
        // Search panel
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnlTimKiem.add(new JLabel("Số điện thoại khách hàng:"));
        txtSoDienThoai = new JTextField(20);
        pnlTimKiem.add(txtSoDienThoai);
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemVeTheoSDT());
        MaterialInitializer.styleButton(btnTimKiem);
        pnlTimKiem.add(btnTimKiem);
        
        pnlCenter.add(pnlTimKiem, BorderLayout.NORTH);
        
        // Ticket table with comprehensive columns
        String[] tenCot = {
            "Mã vé", "Mã hóa đơn", "Ga đi", "Ga đến", 
            "Ngày đi", "Giờ đi", "Toa", "Ghế", "Loại vé", "Giá", "Trạng thái"
        };
        modelBangVe = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblVeKhachHang = new JTable(modelBangVe);
        tblVeKhachHang.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollVe = new JScrollPane(tblVeKhachHang);
        scrollVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé của khách hàng"));
        MaterialInitializer.setTableScrollPaneSize(scrollVe, 40);
        
        pnlCenter.add(scrollVe, BorderLayout.CENTER);
        add(pnlCenter, BorderLayout.CENTER);
        
        // Button panel
        JPanel pnlButton = MaterialInitializer.createButtonPanel();
        
        btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.addActionListener(e -> moDialogDoiVe());
        MaterialInitializer.styleButton(btnDoiVe);
        pnlButton.add(btnDoiVe);
        
        btnInVe = new JButton("In vé");
        btnInVe.addActionListener(e -> inVeDaChon());
        MaterialInitializer.styleButton(btnInVe);
        pnlButton.add(btnInVe);
        
        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> lamMoi());
        MaterialInitializer.styleButton(btnRefresh);
        pnlButton.add(btnRefresh);
        
        add(pnlButton, BorderLayout.SOUTH);
    }

    /**
     * Tìm kiếm vé theo số điện thoại khách hàng
     */
    private void timKiemVeTheoSDT() {
        String sdt = txtSoDienThoai.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Tìm khách hàng theo SĐT
        khachHangHienTai = khachHangDAO.timTheoSoDienThoai(sdt);
        if (khachHangHienTai == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng với số điện thoại này!", 
                "Thông báo", JOptionPane.WARNING_MESSAGE);
            modelBangVe.setRowCount(0);
            return;
        }
        
        // Lấy tất cả vé của khách hàng
        modelBangVe.setRowCount(0);
        List<HoaDon> danhSachHoaDon = hoaDonDAO.findByKhachHang(khachHangHienTai.getMaKhachHang());
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (HoaDon hoaDon : danhSachHoaDon) {
            List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hoaDon.getMaHoaDon());
            for (ChiTietHoaDon chiTiet : chiTietList) {
                Ve ve = veDAO.findById(chiTiet.getMaVe());
                if (ve != null) {
                    modelBangVe.addRow(new Object[]{
                        ve.getMaVe(),
                        hoaDon.getMaHoaDon(),
                        ve.getGaDi() != null ? ve.getGaDi() : "N/A",
                        ve.getGaDen() != null ? ve.getGaDen() : "N/A",
                        ve.getGioDi() != null ? ve.getGioDi().format(dateFormatter) : "N/A",
                        ve.getGioDi() != null ? ve.getGioDi().format(timeFormatter) : "N/A",
                        ve.getSoToa() != null ? ve.getSoToa() : "N/A",
                        ve.getMaSoGhe() != null ? ve.getMaSoGhe() : "N/A",
                        ve.getLoaiVe() != null ? ve.getLoaiVe() : "N/A",
                        String.format("%,.0f VNĐ", chiTiet.getGiaDaKM()),
                        ve.getTrangThai() != null ? ve.getTrangThai() : "N/A"
                    });
                }
            }
        }
        
        if (modelBangVe.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Khách hàng chưa có vé nào!", 
                "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Mở dialog để đổi vé
     */
    private void moDialogDoiVe() {
        int row = tblVeKhachHang.getSelectedRow();
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
        
        // Validate vé có thể đổi không
        DoiVeResult validation = veService.validateDoiVe(maVe);
        if (!validation.isThanhCong()) {
            JOptionPane.showMessageDialog(this, validation.getThongBao(), "Không thể đổi vé", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Mở dialog đổi vé
        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        new DlgDoiVe(parentFrame, veHienTai).setVisible(true);
    }

    /**
     * In vé đã chọn
     */
    private void inVeDaChon() {
        int row = tblVeKhachHang.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần in!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maVe = (String) modelBangVe.getValueAt(row, 0);
        Ve ve = veService.timVeTheoMa(maVe);
        
        if (ve == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            String filePath = veService.inVePDF(ve);
            JOptionPane.showMessageDialog(this, "Đã in vé thành công!\nFile: " + filePath, 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            // Mở file PDF
            Desktop.getDesktop().open(new java.io.File(filePath));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi in vé: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Làm mới dữ liệu
     */
    private void lamMoi() {
        if (khachHangHienTai != null && !txtSoDienThoai.getText().trim().isEmpty()) {
            timKiemVeTheoSDT();
        } else {
            modelBangVe.setRowCount(0);
            txtSoDienThoai.setText("");
        }
    }

    /**
     * Dialog đổi vé với giao diện đầy đủ
     */
    private class DlgDoiVe extends JDialog {
        private Ve veCu;
        private JComboBox<String> cmbGaDi;
        private JComboBox<String> cmbGaDen;
        private JDateChooser dateNgayDi;
        private JButton btnTimChuyen;
        private JTable tblChuyenTau;
        private DefaultTableModel modelChuyen;
        private JComboBox<String> cmbToa;
        private JPanel pnlSoDoGhe;
        private JTextArea txtGhiChu;
        private JLabel lblGiaVeCu;
        private JLabel lblGiaVeMoi;
        private JLabel lblChenhLech;
        
        private ChuyenTau chuyenDaChon;
        private Ghe gheDaChon;
        private String toaDaChon;

        public DlgDoiVe(Frame owner, Ve veCu) {
            super((Frame) SwingUtilities.getWindowAncestor(owner), "Đổi vé", true);
            this.veCu = veCu;
            initDialog();
        }

        private void initDialog() {
            setLayout(new BorderLayout(10, 10));
            setSize(1000, 700);
            setLocationRelativeTo(getOwner());

            // Main content panel
            JPanel pnlMain = new JPanel(new GridLayout(1, 2, 10, 10));
            pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Left panel - Old ticket info
            pnlMain.add(taoThongTinVeCu());

            // Right panel - New ticket selection
            pnlMain.add(taoChonVeMoi());

            add(pnlMain, BorderLayout.CENTER);

            // Bottom panel - Buttons
            JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
            
            JButton btnXacNhan = new JButton("Xác nhận đổi vé");
            btnXacNhan.addActionListener(e -> xacNhanDoiVe());
            MaterialInitializer.styleButton(btnXacNhan);
            pnlButtons.add(btnXacNhan);
            
            JButton btnHuy = new JButton("Hủy");
            btnHuy.addActionListener(e -> dispose());
            MaterialInitializer.styleButton(btnHuy);
            pnlButtons.add(btnHuy);
            
            add(pnlButtons, BorderLayout.SOUTH);
        }

        private JPanel taoThongTinVeCu() {
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBorder(BorderFactory.createTitledBorder("Thông tin vé cũ"));

            JPanel pnlInfo = new JPanel(new GridLayout(0, 2, 5, 5));
            pnlInfo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            addLabelValue(pnlInfo, "Mã vé:", veCu.getMaVe());
            addLabelValue(pnlInfo, "Ga đi:", veCu.getGaDi());
            addLabelValue(pnlInfo, "Ga đến:", veCu.getGaDen());
            addLabelValue(pnlInfo, "Ngày giờ đi:", veCu.getGioDi() != null ? veCu.getGioDi().format(formatter) : "N/A");
            addLabelValue(pnlInfo, "Toa:", veCu.getSoToa());
            addLabelValue(pnlInfo, "Ghế:", veCu.getMaSoGhe());
            addLabelValue(pnlInfo, "Loại vé:", veCu.getLoaiVe());
            
            // Giá vé cũ
            ChiTietHoaDon chiTiet = chiTietHoaDonDAO.findByMaVe(veCu.getMaVe());
            float giaVeCu = chiTiet != null ? chiTiet.getGiaDaKM() : 0;
            lblGiaVeCu = new JLabel(String.format("%,.0f VNĐ", giaVeCu));
            lblGiaVeCu.setFont(lblGiaVeCu.getFont().deriveFont(Font.BOLD));
            addLabelValue(pnlInfo, "Giá vé:", lblGiaVeCu);

            panel.add(pnlInfo, BorderLayout.NORTH);

            return panel;
        }

        private JPanel taoChonVeMoi() {
            JPanel panel = new JPanel(new BorderLayout(5, 5));
            panel.setBorder(BorderFactory.createTitledBorder("Chọn vé mới"));

            // Top - Search filters
            JPanel pnlFilter = new JPanel(new GridLayout(0, 2, 5, 5));
            pnlFilter.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            // Ga đi
            pnlFilter.add(new JLabel("Ga đi:"));
            cmbGaDi = new JComboBox<>(new String[]{
                "Hà Nội", "Sài Gòn", "Đà Nẵng", "Huế", "Nha Trang", "Vinh"
            });
            cmbGaDi.setSelectedItem(veCu.getGaDi());
            pnlFilter.add(cmbGaDi);

            // Ga đến
            pnlFilter.add(new JLabel("Ga đến:"));
            cmbGaDen = new JComboBox<>(new String[]{
                "Sài Gòn", "Hà Nội", "Đà Nẵng", "Huế", "Nha Trang", "Vinh"
            });
            cmbGaDen.setSelectedItem(veCu.getGaDen());
            pnlFilter.add(cmbGaDen);

            // Ngày đi
            pnlFilter.add(new JLabel("Ngày đi:"));
            dateNgayDi = new JDateChooser();
            dateNgayDi.setDateFormatString("dd/MM/yyyy");
            if (veCu.getGioDi() != null) {
                dateNgayDi.setDate(java.sql.Date.valueOf(veCu.getGioDi().toLocalDate()));
            }
            pnlFilter.add(dateNgayDi);

            // Nút tìm chuyến
            btnTimChuyen = new JButton("Tìm chuyến");
            btnTimChuyen.addActionListener(e -> timChuyenTau());
            MaterialInitializer.styleButton(btnTimChuyen);
            pnlFilter.add(new JLabel(""));
            pnlFilter.add(btnTimChuyen);

            panel.add(pnlFilter, BorderLayout.NORTH);

            // Middle - Trains table
            String[] columns = {"Mã chuyến", "Tàu", "Ga đi", "Ga đến", "Giờ đi", "Khoảng cách"};
            modelChuyen = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            tblChuyenTau = new JTable(modelChuyen);
            tblChuyenTau.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    chonChuyenTau();
                }
            });
            JScrollPane scrollChuyen = new JScrollPane(tblChuyenTau);
            scrollChuyen.setPreferredSize(new Dimension(0, 150));
            panel.add(scrollChuyen, BorderLayout.CENTER);

            // Bottom - Seat selection and summary
            JPanel pnlBottom = new JPanel(new BorderLayout(5, 5));
            
            // Toa selector
            JPanel pnlToa = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pnlToa.add(new JLabel("Chọn toa:"));
            cmbToa = new JComboBox<>();
            cmbToa.addActionListener(e -> loadGhe());
            pnlToa.add(cmbToa);
            pnlBottom.add(pnlToa, BorderLayout.NORTH);

            // Seat map
            pnlSoDoGhe = new JPanel(new GridLayout(0, 5, 5, 5));
            JScrollPane scrollGhe = new JScrollPane(pnlSoDoGhe);
            scrollGhe.setPreferredSize(new Dimension(0, 150));
            pnlBottom.add(scrollGhe, BorderLayout.CENTER);

            // Summary and notes
            JPanel pnlSummary = new JPanel(new GridLayout(0, 1, 5, 5));
            pnlSummary.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JPanel pnlGia = new JPanel(new GridLayout(3, 2, 5, 5));
            pnlGia.add(new JLabel("Giá vé mới:"));
            lblGiaVeMoi = new JLabel("0 VNĐ");
            lblGiaVeMoi.setFont(lblGiaVeMoi.getFont().deriveFont(Font.BOLD));
            pnlGia.add(lblGiaVeMoi);
            
            pnlGia.add(new JLabel("Chênh lệch:"));
            lblChenhLech = new JLabel("0 VNĐ");
            lblChenhLech.setFont(lblChenhLech.getFont().deriveFont(Font.BOLD, 14f));
            pnlGia.add(lblChenhLech);
            
            pnlSummary.add(pnlGia);
            
            pnlSummary.add(new JLabel("Ghi chú/Lý do:"));
            txtGhiChu = new JTextArea(3, 20);
            txtGhiChu.setLineWrap(true);
            txtGhiChu.setWrapStyleWord(true);
            pnlSummary.add(new JScrollPane(txtGhiChu));
            
            pnlBottom.add(pnlSummary, BorderLayout.SOUTH);

            panel.add(pnlBottom, BorderLayout.SOUTH);

            return panel;
        }

        private void addLabelValue(JPanel panel, String label, String value) {
            JLabel lbl = new JLabel(label);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            panel.add(lbl);
            panel.add(new JLabel(value != null ? value : "N/A"));
        }

        private void addLabelValue(JPanel panel, String label, JLabel valueLabel) {
            JLabel lbl = new JLabel(label);
            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
            panel.add(lbl);
            panel.add(valueLabel);
        }

        private void timChuyenTau() {
            String gaDi = (String) cmbGaDi.getSelectedItem();
            String gaDen = (String) cmbGaDen.getSelectedItem();
            
            if (gaDi == null || gaDen == null || gaDi.equals(gaDen)) {
                JOptionPane.showMessageDialog(this, "Ga đi và ga đến phải khác nhau!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (dateNgayDi.getDate() == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày đi!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            modelChuyen.setRowCount(0);
            
            List<ChuyenTau> danhSachChuyen = chuyenTauDAO.getAll();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            
            for (ChuyenTau chuyen : danhSachChuyen) {
                if (chuyen.getGaDi() != null && chuyen.getGaDi().equals(gaDi) &&
                    chuyen.getGaDen() != null && chuyen.getGaDen().equals(gaDen)) {
                    modelChuyen.addRow(new Object[]{
                        chuyen.getMaChuyen(),
                        chuyen.getMaTau(),
                        chuyen.getGaDi(),
                        chuyen.getGaDen(),
                        chuyen.getGioDi() != null ? chuyen.getGioDi().format(formatter) : "N/A",
                        chuyen.getSoKm() + " km"
                    });
                }
            }
            
            if (modelChuyen.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến tàu phù hợp!", 
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private void chonChuyenTau() {
            int row = tblChuyenTau.getSelectedRow();
            if (row < 0) return;
            
            String maChuyen = (String) modelChuyen.getValueAt(row, 0);
            chuyenDaChon = chuyenTauDAO.findById(maChuyen);
            
            if (chuyenDaChon == null) return;
            
            // Load danh sách toa
            cmbToa.removeAllItems();
            List<ToaTau> danhSachToa = toaTauDAO.getByTau(chuyenDaChon.getMaTau());
            for (ToaTau toa : danhSachToa) {
                cmbToa.addItem(toa.getMaToa());
            }
            
            if (cmbToa.getItemCount() > 0) {
                cmbToa.setSelectedIndex(0);
            }
        }

        private void loadGhe() {
            pnlSoDoGhe.removeAll();
            gheDaChon = null;
            
            if (cmbToa.getSelectedItem() == null) return;
            
            toaDaChon = (String) cmbToa.getSelectedItem();
            List<Ghe> danhSachGhe = gheDAO.getByToa(toaDaChon);
            
            for (Ghe ghe : danhSachGhe) {
                JButton btnGhe = new JButton(ghe.getMaGhe());
                
                if ("Trống".equals(ghe.getTrangThai()) || "Rảnh".equals(ghe.getTrangThai())) {
                    btnGhe.setBackground(new Color(34, 139, 34));
                    btnGhe.setForeground(Color.WHITE);
                    btnGhe.addActionListener(evt -> {
                        gheDaChon = ghe;
                        capNhatGheDaChon(btnGhe);
                        tinhGiaVeMoi();
                    });
                } else {
                    btnGhe.setBackground(Color.GRAY);
                    btnGhe.setForeground(Color.WHITE);
                    btnGhe.setEnabled(false);
                }
                
                pnlSoDoGhe.add(btnGhe);
            }
            
            pnlSoDoGhe.revalidate();
            pnlSoDoGhe.repaint();
        }

        private void capNhatGheDaChon(JButton btnSelected) {
            // Reset all buttons
            for (Component comp : pnlSoDoGhe.getComponents()) {
                if (comp instanceof JButton) {
                    JButton btn = (JButton) comp;
                    if (btn.isEnabled()) {
                        btn.setBackground(new Color(34, 139, 34));
                    }
                }
            }
            // Highlight selected
            btnSelected.setBackground(Color.ORANGE);
        }

        private void tinhGiaVeMoi() {
            if (chuyenDaChon == null || gheDaChon == null) {
                lblGiaVeMoi.setText("0 VNĐ");
                lblChenhLech.setText("0 VNĐ");
                return;
            }
            
            // Tạo vé tạm để tính giá
            Ve veTam = new Ve();
            veTam.setMaChuyen(chuyenDaChon.getMaChuyen());
            veTam.setMaSoGhe(gheDaChon.getMaGhe());
            veTam.setGaDi(chuyenDaChon.getGaDi());
            veTam.setGaDen(chuyenDaChon.getGaDen());
            veTam.setGioDi(chuyenDaChon.getGioDi());
            veTam.setSoToa(toaDaChon);
            veTam.setLoaiCho(gheDaChon.getLoaiGhe());
            veTam.setMaLoaiVe(veCu.getMaLoaiVe());
            veTam.setLoaiVe(veCu.getLoaiVe());
            
            try {
                TinhGiaService tinhGiaService = TinhGiaService.getInstance();
                TinhGiaService.KetQuaGia ketQuaGia = tinhGiaService.tinhGiaChoVe(veTam);
                
                ChiTietHoaDon chiTiet = chiTietHoaDonDAO.findByMaVe(veCu.getMaVe());
                float giaVeCu = chiTiet != null ? chiTiet.getGiaDaKM() : 0;
                float giaVeMoi = ketQuaGia.giaDaKM;
                float chenhLech = giaVeMoi - giaVeCu;
                
                lblGiaVeMoi.setText(String.format("%,.0f VNĐ", giaVeMoi));
                lblChenhLech.setText(String.format("%+,.0f VNĐ", chenhLech));
                
                if (chenhLech > 0) {
                    lblChenhLech.setForeground(Color.RED);
                } else if (chenhLech < 0) {
                    lblChenhLech.setForeground(new Color(0, 128, 0));
                } else {
                    lblChenhLech.setForeground(Color.BLACK);
                }
            } catch (Exception ex) {
                lblGiaVeMoi.setText("Không tính được");
                lblChenhLech.setText("N/A");
            }
        }

        private void xacNhanDoiVe() {
            if (chuyenDaChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến tàu mới!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (gheDaChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế mới!", 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Tạo request đổi vé
            DoiVeRequest request = new DoiVeRequest();
            request.setMaVeCu(veCu.getMaVe());
            request.setMaChuyenMoi(chuyenDaChon.getMaChuyen());
            request.setMaToaMoi(toaDaChon);
            request.setMaGheMoi(gheDaChon.getMaGhe());
            request.setMaLoaiVeMoi(veCu.getMaLoaiVe());
            request.setLyDo(txtGhiChu.getText().trim());
            request.setMaNV(taiKhoanHienTai.getMaNV());
            
            // Thực hiện đổi vé
            try {
                DoiVeResult result = veService.yeuCauDoiVe(request);
                
                if (result.isThanhCong()) {
                    String message = "Đổi vé thành công!\n\n";
                    
                    if (result.isCanThanhToan()) {
                        message += String.format("Khách hàng cần thanh toán thêm: %,.0f VNĐ", result.getChenhLechGia());
                    } else if (result.isCanHoanTien()) {
                        message += String.format("Khách hàng được hoàn lại: %,.0f VNĐ", Math.abs(result.getChenhLechGia()));
                    } else {
                        message += "Không có chênh lệch giá.";
                    }
                    
                    JOptionPane.showMessageDialog(this, message, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    
                    // In vé mới
                    int option = JOptionPane.showConfirmDialog(this, "Bạn có muốn in vé mới?", 
                        "In vé", JOptionPane.YES_NO_OPTION);
                    if (option == JOptionPane.YES_OPTION) {
                        try {
                            String filePath = veService.inVePDF(result.getVeMoi());
                            Desktop.getDesktop().open(new java.io.File(filePath));
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this, "Không thể in vé: " + ex.getMessage());
                        }
                    }
                    
                    dispose();
                    lamMoi(); // Refresh danh sách vé
                } else {
                    JOptionPane.showMessageDialog(this, result.getThongBao(), 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi đổi vé: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
