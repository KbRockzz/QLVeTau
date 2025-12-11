package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Calendar;
import java.util.Date;

import com.toedter.calendar.JDateChooser;

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

    private static final boolean ALLOW_SAME_DAY = true;
    private static final int MIN_ADVANCE_MINUTES = 60; // minimum
    private static final int MAX_DAYS_AHEAD = 90; // maximum

    // Customer
    private JTextField txtSoDienThoai;
    private JButton btnTimKhachHang;
    private JLabel lblThongTinKhachHang;
    private KhachHang khachHangDuocChon;

    // Train
    private JComboBox<String> cmbGaDi;
    private JComboBox<String> cmbGaDen;
    private JDateChooser dateNgayDi;
    private JSpinner spnGioDi;
    private JButton btnTimChuyenTau;
    private JTable tblChuyenTau;
    private DefaultTableModel modelBangChuyenTau;

    private JComboBox<String> cboLoaiVe;
    private JTable bangToaTau;
    private DefaultTableModel modelBangToa;
    private JPanel pnlSoDoGhe;
    private ChuyenTau chuyenDuocChon;
    private ToaTau toaDuocChon;
    private Ghe gheDuocChon;

    // New
    private HoaDon hoaDonMo;
    private final List<Ve> danhSachVeTrongHoaDon = new ArrayList<>();
    private JButton btnThanhToan; // nút xác nhận thanh toán (finalize)

    private final Map<String, Ve> heldVeMap = new HashMap<>();

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

        // Top panel
        JPanel pnlTop = new JPanel(new BorderLayout(5, 5));

        // Title
        JLabel lblTieuDe = new JLabel("ĐẶT VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        pnlTop.add(lblTieuDe, BorderLayout.NORTH);

        // Customer search panel
        JPanel pnlTimKhachHang = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTimKhachHang.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));

        pnlTimKhachHang.add(new JLabel("Số điện thoại:"));
        txtSoDienThoai = new JTextField(15);
        pnlTimKhachHang.add(txtSoDienThoai);

        btnTimKhachHang = new JButton("Tìm khách hàng");
        btnTimKhachHang.addActionListener(e -> timKhachHang());
        MaterialInitializer.styleButton(btnTimKhachHang);
        pnlTimKhachHang.add(btnTimKhachHang);

//        lblThongTinKhachHang = new JLabel("(Chưa chọn khách hàng)");
//        lblThongTinKhachHang.setForeground(Color.BLUE);
//        pnlTimKhachHang.add(lblThongTinKhachHang);

        // Ticket
        pnlTimKhachHang.add(Box.createHorizontalStrut(20));
        pnlTimKhachHang.add(new JLabel("Loại vé:"));
        cboLoaiVe = new JComboBox<>();
        cboLoaiVe.setPreferredSize(new Dimension(150, 32));
        pnlTimKhachHang.add(cboLoaiVe);

        // Add payment button
        btnThanhToan = new JButton("Xác nhận thanh toán");
        btnThanhToan.setToolTipText("Thanh toán cho hóa đơn đang mở (nếu có vé được thêm)");
        btnThanhToan.addActionListener(e -> xacNhanThanhToan());
        MaterialInitializer.styleButton(btnThanhToan);
        pnlTimKhachHang.add(Box.createHorizontalStrut(10));
        pnlTimKhachHang.add(btnThanhToan);

        pnlTop.add(pnlTimKhachHang, BorderLayout.CENTER);

        // Train search
        JPanel pnlTimChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTimChuyenTau.setBorder(BorderFactory.createTitledBorder("Tìm chuyến tàu"));

        pnlTimChuyenTau.add(new JLabel("Ga đi:"));
        cmbGaDi = new JComboBox<>();
        cmbGaDi.setPreferredSize(new Dimension(180, 32));
        pnlTimChuyenTau.add(cmbGaDi);

        pnlTimChuyenTau.add(new JLabel("Ga đến:"));
        cmbGaDen = new JComboBox<>();
        cmbGaDen.setPreferredSize(new Dimension(180, 32));
        pnlTimChuyenTau.add(cmbGaDen);

        pnlTimChuyenTau.add(new JLabel("Ngày đi:"));
        dateNgayDi = new JDateChooser();
        dateNgayDi.setPreferredSize(new Dimension(180, 32));
        dateNgayDi.setDateFormatString("dd/MM/yyyy");
        // Set min/max
        applyDateConstraintsToDateChooser();
        // Validate
        dateNgayDi.getDateEditor().addPropertyChangeListener("date", evt -> {
            Date selected = dateNgayDi.getDate();
            if (selected == null) return;
            LocalDate sel = selected.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate min = computeEarliestAllowedDate();
            LocalDate max = LocalDate.now().plusDays(MAX_DAYS_AHEAD);
            if (sel.isBefore(min) || sel.isAfter(max)) {
                JOptionPane.showMessageDialog(this,
                        String.format("Vui lòng chọn ngày trong khoảng từ %s đến %s",
                                min.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                                max.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))),
                        "Ngày không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                // reset to min
                dateNgayDi.setDate(java.util.Date.from(min.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            }
        });

        pnlTimChuyenTau.add(dateNgayDi);

        pnlTimChuyenTau.add(new JLabel("Giờ đi (từ):"));
        SpinnerDateModel timeModel = new SpinnerDateModel();
        spnGioDi = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGioDi, "HH:mm");
        spnGioDi.setEditor(timeEditor);
        spnGioDi.setPreferredSize(new Dimension(100, 32));
        pnlTimChuyenTau.add(spnGioDi);

        btnTimChuyenTau = new JButton("Tìm chuyến tàu");
        btnTimChuyenTau.addActionListener(e -> timChuyenTau());
        MaterialInitializer.styleButton(btnTimChuyenTau);
        pnlTimChuyenTau.add(btnTimChuyenTau);

        pnlTop.add(pnlTimChuyenTau, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // Main content
        JPanel pnlNoiDung = new JPanel(new BorderLayout(10, 10));

        // Top section
        String[] tenCotChuyenTau = {"Mã chuyến", "Tên tàu", "Ga đi", "Ga đến", "Ngày đi", "Giờ đi", "Giờ đến"};
        modelBangChuyenTau = new DefaultTableModel(tenCotChuyenTau, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblChuyenTau = new JTable(modelBangChuyenTau);
        tblChuyenTau.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                chonChuyenTauTuBang();
            }
        });
        JScrollPane scrollChuyenTau = new JScrollPane(tblChuyenTau);
        scrollChuyenTau.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến tàu"));
        // Giảm chiều cao bảng để có đủ không gian cho form phía dưới
        scrollChuyenTau.setPreferredSize(new Dimension(0, 120));
        pnlNoiDung.add(scrollChuyenTau, BorderLayout.NORTH);

        // Bottom section
        JPanel pnlDuoi = new JPanel(new BorderLayout(10, 10));

        // Left panel
        JPanel pnlTrai = new JPanel(new BorderLayout(5, 5));
        pnlTrai.setPreferredSize(new Dimension(400, 0));

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

        pnlDuoi.add(pnlTrai, BorderLayout.WEST);

        // Right panel
        pnlSoDoGhe = new JPanel();
        JScrollPane scrollGhe = new JScrollPane(pnlSoDoGhe);
        scrollGhe.setBorder(BorderFactory.createTitledBorder("Sơ đồ ghế (Bố trí toa tàu)"));
        pnlDuoi.add(scrollGhe, BorderLayout.CENTER);

        pnlNoiDung.add(pnlDuoi, BorderLayout.CENTER);

        add(pnlNoiDung, BorderLayout.CENTER);

        // Legend panel
        JPanel pnlChuThich = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblTrong = new JLabel("■ Trống");
        lblTrong.setForeground(new Color(34, 139, 34));
        JLabel lblDaDat = new JLabel("■ Đã đặt");
        lblDaDat.setForeground(Color.RED);
        JLabel lblDaGiu = new JLabel("■ Đang giữ (chưa thanh toán)");
        lblDaGiu.setForeground(Color.BLUE);
        pnlChuThich.add(lblTrong);
        pnlChuThich.add(Box.createHorizontalStrut(20));
        pnlChuThich.add(lblDaDat);
        pnlChuThich.add(Box.createHorizontalStrut(20));
        pnlChuThich.add(lblDaGiu);
        add(pnlChuThich, BorderLayout.SOUTH);

        // Load data
        taiDanhSachGa();
        taiDanhSachLoaiVe();
    }

    /**
     * Quy định ngày đặt vé hợp lệ dựa trên cài đặt.
     */
    private LocalDate computeEarliestAllowedDate() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime earliest = now.plusMinutes(MIN_ADVANCE_MINUTES);
        LocalDate earliestDate = earliest.toLocalDate();
        if (!ALLOW_SAME_DAY) {
            // require strictly after today -> earliestDate is tomorrow
            return LocalDate.now().plusDays(1);
        } else {
            return earliestDate;
        }
    }

    /**
     * Áp dụng ràng buộc ngày cho JDateChooser
     */
    private void applyDateConstraintsToDateChooser() {
        LocalDate minDate = computeEarliestAllowedDate();
        LocalDate maxDate = LocalDate.now().plusDays(MAX_DAYS_AHEAD);

        Date min = java.util.Date.from(minDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date max = java.util.Date.from(maxDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        dateNgayDi.setMinSelectableDate(min);
        dateNgayDi.setMaxSelectableDate(max);

        dateNgayDi.setDate(min);
    }

    private void taiDanhSachGa() {
        cmbGaDi.removeAllItems();
        cmbGaDen.removeAllItems();
        cmbGaDi.addItem(""); // Empty option
        cmbGaDen.addItem(""); // Empty option
        List<String> danhSachGa = chuyenTauDAO.getDistinctStations();
        for (String ga : danhSachGa) {
            cmbGaDi.addItem(ga);
            cmbGaDen.addItem(ga);
        }
    }

    private void taiDanhSachLoaiVe() {
        cboLoaiVe.removeAllItems();
        List<LoaiVe> danhSach = loaiVeDAO.getAll();
        for (LoaiVe lv : danhSach) {
            cboLoaiVe.addItem(lv.getTenLoai());
        }
    }

    private void timKhachHang() {
        String sdt = txtSoDienThoai.getText().trim();
        if (sdt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        khachHangDuocChon = khachHangDAO.timTheoSoDienThoai(sdt);

        if (khachHangDuocChon != null) {
            lblThongTinKhachHang.setText("Khách hàng: " + khachHangDuocChon.getTenKhachHang() + " (Mã: " + khachHangDuocChon.getMaKhachHang() + ")");
            lblThongTinKhachHang.setForeground(new Color(0, 128, 0));

            // Nếu khách có hoá đơn mở, load vào hoaDonMo và danhSachVeTrongHoaDon
            List<HoaDon> danhSachHoaDon = HoaDonDAO.getInstance().findByKhachHang(khachHangDuocChon.getMaKhachHang());
            for (HoaDon hd : danhSachHoaDon) {
                if ("Chờ xác nhận".equals(hd.getTrangThai())) {
                    hoaDonMo = hd;
                    // Load vé trong hoá đơn mở
                    danhSachVeTrongHoaDon.clear();
                    // Giả sử VeDAO có phương thức tìm vé theo mã hoá đơn
                    break;
                }
            }
        } else {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Khách hàng chưa tồn tại. Bạn có muốn tạo khách hàng mới không?",
                    "Khách hàng không tồn tại",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                hienThiFormThemKhachHang(sdt);
            }
        }
    }

    private void timChuyenTau() {
        // Tìm kiếm chuyến tàu dựa trên tiêu chí
        String gaDi = (String) cmbGaDi.getSelectedItem();
        if (gaDi != null && gaDi.trim().isEmpty()) {
            gaDi = null;
        }

        String gaDen = (String) cmbGaDen.getSelectedItem();
        if (gaDen != null && gaDen.trim().isEmpty()) {
            gaDen = null;
        }

        LocalDate ngayDi = null;
        if (dateNgayDi.getDate() != null) {
            ngayDi = dateNgayDi.getDate().toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDate();
        }

        LocalTime gioDi = null;
        if (spnGioDi.getValue() != null) {
            java.util.Date date = (java.util.Date) spnGioDi.getValue();
            gioDi = date.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalTime();
        }

        // Ràng buộc thời gian đặt vé
        if (ngayDi != null && gioDi != null) {
            LocalDateTime selectedDT = LocalDateTime.of(ngayDi, gioDi);
            if (!isBookingAllowedForDateTime(selectedDT)) {
                // message already shown in isBookingAllowedForDateTime
                return;
            }
        }

        List<ChuyenTau> ketQua = chuyenTauDAO.timKiemChuyenTau(gaDi, gaDen, ngayDi, gioDi);

        //
        modelBangChuyenTau.setRowCount(0);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (ChuyenTau ct : ketQua) {
            String ngayDiStr = ct.getGioDi() != null ? ct.getGioDi().format(dateFormatter) : "";
            String gioDiStr = ct.getGioDi() != null ? ct.getGioDi().format(timeFormatter) : "";
            String gioDenStr = ct.getGioDen() != null ? ct.getGioDen().format(timeFormatter) : "";

            modelBangChuyenTau.addRow(new Object[]{
                    ct.getMaChuyen(),
                    ct.getMaTau(),
                    ct.getGaDi(),
                    ct.getGaDen(),
                    ngayDiStr,
                    gioDiStr,
                    gioDenStr
            });
        }

        if (ketQua.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến tàu phù hợp!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void chonChuyenTauTuBang() {
        int row = tblChuyenTau.getSelectedRow();
        if (row < 0) return;

        String maChuyen = (String) modelBangChuyenTau.getValueAt(row, 0);
        chuyenDuocChon = chuyenTauDAO.findById(maChuyen);

        if (chuyenDuocChon == null) return;

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

        // Load Ghế
        hienThiSoDoGhe(maToa);
    }

    private void hienThiSoDoGhe(String maToa) {
        pnlSoDoGhe.removeAll();
        List<Ghe> danhSachGhe = gheDAO.getByToa(maToa);

        if (danhSachGhe.isEmpty()) {
            pnlSoDoGhe.setLayout(new FlowLayout());
            pnlSoDoGhe.add(new JLabel("Không có ghế nào trong toa này"));
        } else {

            int soGhe = danhSachGhe.size();
            int soHang = (int) Math.ceil(soGhe / 4.0);


            pnlSoDoGhe.setLayout(new GridLayout(soHang, 5, 5, 5));

            for (int i = 0; i < soHang; i++) {

                for (int j = 0; j < 2; j++) {
                    int index = i * 4 + j;
                    if (index < soGhe) {
                        pnlSoDoGhe.add(taoNutGhe(danhSachGhe.get(index)));
                    } else {
                        pnlSoDoGhe.add(new JLabel(""));
                    }
                }

                JPanel pnlLoiDi = new JPanel();
                pnlLoiDi.setBackground(new Color(200, 200, 200));
                pnlLoiDi.setPreferredSize(new Dimension(30, 40));
                pnlSoDoGhe.add(pnlLoiDi);

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
        if ("Rảnh".equalsIgnoreCase(ghe.getTrangThai())) {
            btnGhe.setBackground(new Color(34, 139, 34)); // Xanh
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(true);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Trống");
            btnGhe.addActionListener(e -> chonGhe(ghe));
        } else {
            btnGhe.setBackground(Color.RED);
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(false);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Đã đặt");
        }
        if (heldVeMap.containsKey(ghe.getMaGhe())) {
            // hiển thị as 'Đã giữ' (blue)
            btnGhe.setBackground(Color.BLUE);
            btnGhe.setEnabled(false);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Đang giữ (chưa thanh toán)");
            return btnGhe;
        }
        return btnGhe;
    }

    private void chonGhe(Ghe ghe) {
        gheDuocChon = ghe;

        // Ràng buộc trước khi đặt vé
        if (khachHangDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm và chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (cboLoaiVe.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn loại vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        xacNhanDatVe();
    }

    private void xacNhanDatVe() {
        String loaiVeStr = (String) cboLoaiVe.getSelectedItem();

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

        if (chuyenDuocChon == null || chuyenDuocChon.getGioDi() == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn chuyến hoặc chuyến không có thời gian.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        LocalDateTime tripDateTime = chuyenDuocChon.getGioDi();
        if (!isBookingAllowedForDateTime(tripDateTime)) {
            return;
        }

        String message = String.format(
                "Xác nhận đặt vé:\n\n" +
                        "Khách hàng: %s\n" +
                        "Chuyến tàu: %s → %s\n" +
                        "Toa: %s\n" +
                        "Ghế: %s\n" +
                        "Loại vé: %s\n\n" +
                        "Bạn có chắc chắn muốn đặt vé này?",
                khachHangDuocChon.getTenKhachHang(),
                chuyenDuocChon.getGaDi(), chuyenDuocChon.getGaDen(),
                toaDuocChon.getTenToa(),
                gheDuocChon.getMaGhe(),
                loaiVe.getTenLoai()
        );

        int choice = JOptionPane.showConfirmDialog(this, message, "Xác nhận đặt vé",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            datVe(khachHangDuocChon, loaiVe);
        }
    }
    private boolean isBookingAllowedForDateTime(LocalDateTime tripDateTime) {
        if (tripDateTime == null) {
            JOptionPane.showMessageDialog(this, "Thời gian chuyến không hợp lệ.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (!ALLOW_SAME_DAY) {
            if (!tripDateTime.toLocalDate().isAfter(now.toLocalDate())) {
                JOptionPane.showMessageDialog(this,
                        "Chỉ được đặt vé cho các chuyến vào ngày sau ngày hôm nay.",
                        "Không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        } else {
            LocalDateTime earliest = now.plusMinutes(MIN_ADVANCE_MINUTES);
            if (tripDateTime.isBefore(earliest)) {
                JOptionPane.showMessageDialog(this,
                        String.format("Không thể đặt vé cho chuyến trước %d phút so với thời điểm hiện tại.", MIN_ADVANCE_MINUTES),
                        "Không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        LocalDateTime latest = now.plusDays(MAX_DAYS_AHEAD).with(LocalTime.MAX);
        if (tripDateTime.isAfter(latest)) {
            JOptionPane.showMessageDialog(this,
                    String.format("Hiện chỉ cho phép đặt vé trong %d ngày tới.", MAX_DAYS_AHEAD),
                    "Không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void hienThiFormThemKhachHang(String soDienThoai) {
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
        txtSDT.setText(soDienThoai); // Pre-fill with searched phone
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

            if (khachHangDAO.findById(maKH) != null) {
                JOptionPane.showMessageDialog(dialog, "Mã khách hàng đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String email = txtCCCD.getText().trim() + "|" + txtDiaChi.getText().trim();
            KhachHang kh = new KhachHang(maKH, tenKH, email, sdt);

            if (khachHangDAO.insert(kh)) {
                JOptionPane.showMessageDialog(dialog, "Thêm khách hàng thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                khachHangDuocChon = kh;
                lblThongTinKhachHang.setText("Khách hàng: " + kh.getTenKhachHang() + " (Mã: " + kh.getMaKhachHang() + ")");
                lblThongTinKhachHang.setForeground(new Color(0, 128, 0));
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

    /**
     * Thực hiện đặt vé (thêm vé tạm vào hoá đơn mở trong session).
     *
     * phần "áp dụng bảng giá" đã được tinh chỉnh:
     *  - Lấy maChang từ object ChuyenTau nếu có (chứ không dùng maChuyen trực tiếp).
     *  - Cố gắng resolve mã loại ghế từ bản ghi Ghe trong DB (qua getter phổ biến hoặc reflection).
     *  - Gọi BangGiaDAO.findApplicable(maChang, loaiGheKey, LocalDateTime.now()) để lấy bảng giá đang áp dụng tại thời điểm đặt.
     */
    private void datVe(KhachHang khachHang, LoaiVe loaiVe) {
        try {
            // Validate trip datetime again before actually adding
            if (chuyenDuocChon == null || chuyenDuocChon.getGioDi() == null) {
                JOptionPane.showMessageDialog(this, "Chưa chọn chuyến hoặc chuyến không có thời gian.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            LocalDateTime tripDateTime = chuyenDuocChon.getGioDi();
            if (!isBookingAllowedForDateTime(tripDateTime)) return;

            // 1) Kiểm tra xem ghế đã được giữ trong session này chưa (trước khi tạo đối tượng vé)
            if (gheDuocChon == null) {
                JOptionPane.showMessageDialog(this, "Chưa chọn ghế.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (heldVeMap.containsKey(gheDuocChon.getMaGhe())) {
                JOptionPane.showMessageDialog(this, "Ghế này đã được giữ trong hoá đơn hiện tại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // 2) Kiểm tra trạng thái ghế trên DB
            Ghe gheDB = gheDAO.findById(gheDuocChon.getMaGhe());
            if (gheDB == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy ghế trên hệ thống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String trangThaiDB = gheDB.getTrangThai();
            if (trangThaiDB != null && !"Rảnh".equalsIgnoreCase(trangThaiDB) && !"RANH".equalsIgnoreCase(trangThaiDB)) {
                // Ghế đã bị đặt/giữ bởi người khác
                JOptionPane.showMessageDialog(this, "Ghế này đã không còn trống (" + trangThaiDB + "). Vui lòng chọn ghế khác.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                // refresh seat map để hiển thị trạng thái thực tế
                hienThiSoDoGhe(toaDuocChon.getMaToa());
                return;
            }

            // 3) Tạo mã vé tạm (maVe tạm dùng timestamp)
            String maVe = "VE" + System.currentTimeMillis();

            // 4) Tạo đối tượng Ve (tạm) và gán thuộc tính
            Ve ve = new Ve();
            ve.setMaVe(maVe);
            ve.setMaChuyen(chuyenDuocChon.getMaChuyen());
            ve.setMaLoaiVe(loaiVe.getMaLoaiVe());
            ve.setMaSoGhe(gheDuocChon.getMaGhe());
            ve.setNgayIn(LocalDateTime.now());
            ve.setTrangThai("Chờ xác nhận"); // tạm
            ve.setGaDi(chuyenDuocChon.getGaDi());
            ve.setGaDen(chuyenDuocChon.getGaDen());
            ve.setGioDi(chuyenDuocChon.getGioDi());
            ve.setSoToa(toaDuocChon.getMaToa());
            ve.setLoaiCho(toaDuocChon.getLoaiToa());
            ve.setLoaiVe(loaiVe.getTenLoai());

            // -------------------------
            // 5) Áp dụng bảng giá hiện tại
            // -------------------------
            BangGia bangGia = null;
            try {
                // 1) Lấy maChang từ ChuyenTau (ưu tiên), fallback sang maChuyen nếu null/empty
                String maChang = null;
                try {
                    maChang = chuyenDuocChon.getMaChang();
                } catch (Throwable ignored) {
                }
                if (maChang == null || maChang.trim().isEmpty()) {
                    maChang = chuyenDuocChon.getMaChuyen();
                }

                // 2) Lấy loại ghế từ gheDB.getLoaiGhe()
                String loaiGheKey = null;
                try {
                    loaiGheKey = gheDB.getLoaiGhe();
                } catch (Throwable t) {
                    loaiGheKey = (toaDuocChon != null) ? toaDuocChon.getLoaiToa() : null;
                }
                if (loaiGheKey != null) loaiGheKey = loaiGheKey.trim();

                System.out.println("DEBUG datVe: resolved maChang='" + maChang + "' loaiGheKey='" + loaiGheKey + "' maGhe=" + (gheDuocChon != null ? gheDuocChon.getMaGhe() : "null"));

                // 3) Gọi DAO để tìm bảng giá dựa trên maChang & loaiGheKey & thời điểm đặt (now)
                bangGia = BangGiaDAO.getInstance().findApplicable(maChang, loaiGheKey, LocalDateTime.now());
            } catch (Exception ex) {
                ex.printStackTrace();
                bangGia = null;
            }

            if (bangGia == null) {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy bảng giá phù hợp cho chặng/loại ghế đã chọn. Vui lòng kiểm tra cấu hình bảng giá.",
                        "Bảng giá không tồn tại",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // GÁN maBangGia VÀO Vé TẠM (bắt buộc theo lựa chọn 1)
            ve.setMaBangGia(bangGia.getMaBangGia());
            System.out.println("DEBUG datVe: assigned maBangGia=" + bangGia.getMaBangGia() + " to ve=" + ve.getMaVe());

            // Gán đơn giá nếu model Ve có trường donGia
            try {
                java.lang.reflect.Method mFloat = null;
                try { mFloat = ve.getClass().getMethod("setDonGia", float.class); } catch (NoSuchMethodException ignored) {}
                if (mFloat != null) mFloat.invoke(ve, bangGia.getGiaCoBan());
                else {
                    java.lang.reflect.Method mDouble = null;
                    try { mDouble = ve.getClass().getMethod("setDonGia", double.class); } catch (NoSuchMethodException ignored) {}
                    if (mDouble != null) mDouble.invoke(ve, (double) bangGia.getGiaCoBan());
                    else {
                        try {
                            java.lang.reflect.Method mBig = ve.getClass().getMethod("setDonGia", java.math.BigDecimal.class);
                            mBig.invoke(ve, java.math.BigDecimal.valueOf(bangGia.getGiaCoBan()));
                        } catch (NoSuchMethodException ignored) {}
                    }
                }
            } catch (Throwable ignored) {}

            // -------------------------
            // 6) Thêm vé vào danh sách hoá đơn trong session (in-memory)
            // -------------------------
            // Nếu chưa có hoá đơn mở trong session, tạo 1 hoá đơn tạm
            if (hoaDonMo == null) {
                HoaDon hd = new HoaDon();
                try {
                    hd.setMaHoaDon("HD" + System.currentTimeMillis());
                } catch (Throwable ignored) {
                }
                try { hd.setMaKH(khachHang.getMaKhachHang()); } catch (Throwable ignored) {}
                try { hd.setTrangThai("Chờ xác nhận"); } catch (Throwable ignored) {}
                try { hd.setNgayLap(LocalDateTime.now()); } catch (Throwable ignored) {}
                hoaDonMo = hd;
            }

            // Thêm vé vào danhSachVeTrongHoaDon và mark ghế là giữ tạm trong heldVeMap
            danhSachVeTrongHoaDon.add(ve);
            heldVeMap.put(ve.getMaSoGhe(), ve);

            // Cập nhật UI: đánh dấu ghế đã giữ (hiển thị màu xanh dương)
            JOptionPane.showMessageDialog(this, "Đã thêm vé vào hoá đơn (Giữ ghế tạm). Vui lòng tiến hành 'Xác nhận thanh toán' để hoàn tất.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            if (toaDuocChon != null) hienThiSoDoGhe(toaDuocChon.getMaToa());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đặt vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void xacNhanThanhToan() {
        if (hoaDonMo == null || danhSachVeTrongHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có vé nào trong hóa đơn để thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Tạo combobox với hai lựa chọn
        javax.swing.JComboBox<String> cbPhuongThuc = new javax.swing.JComboBox<>(new String[] { "Chuyển khoản", "Tiền mặt" });
        cbPhuongThuc.setSelectedItem("Tiền mặt"); // mặc định

        int result = JOptionPane.showConfirmDialog(
                this,
                cbPhuongThuc,
                "Chọn phương thức thanh toán",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        // Nếu người dùng hủy hoặc đóng dialog
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String phuongThuc = (String) cbPhuongThuc.getSelectedItem();
        if (phuongThuc == null || phuongThuc.trim().isEmpty()) {
            // Nếu không chọn (không khả dụng), thông báo và thoát
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phương thức thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Gọi checkout; checkout đã được cập nhật để INSERT Ve nếu chưa tồn tại
        boolean ok = HoaDonService.getInstance().checkout(hoaDonMo, new java.util.ArrayList<>(danhSachVeTrongHoaDon), phuongThuc);
        if (ok) {
            // Nếu checkout thành công: clear held memory, reset hoaDonMo
            danhSachVeTrongHoaDon.clear();
            heldVeMap.clear();
            hoaDonMo = null;
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            // Refresh UI
            if (toaDuocChon != null) hienThiSoDoGhe(toaDuocChon.getMaToa());
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}