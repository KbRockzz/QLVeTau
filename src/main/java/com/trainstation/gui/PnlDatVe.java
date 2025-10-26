package com.trainstation.gui;

import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

    // Customer search components
    private JTextField txtSoDienThoai;
    private JButton btnTimKhachHang;
    private JLabel lblThongTinKhachHang;
    private KhachHang khachHangDuocChon;

    // Train search components
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

    // New: current open invoice (hoa don mo) and list of ve added to it
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

        // Top panel with title and customer search
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
        pnlTimKhachHang.add(btnTimKhachHang);

        lblThongTinKhachHang = new JLabel("(Chưa chọn khách hàng)");
        lblThongTinKhachHang.setForeground(Color.BLUE);
        pnlTimKhachHang.add(lblThongTinKhachHang);

        // Ticket type selection
        pnlTimKhachHang.add(Box.createHorizontalStrut(20));
        pnlTimKhachHang.add(new JLabel("Loại vé:"));
        cboLoaiVe = new JComboBox<>();
        cboLoaiVe.setPreferredSize(new Dimension(150, 25));
        pnlTimKhachHang.add(cboLoaiVe);

        // Add payment button to customer panel (right side)
        btnThanhToan = new JButton("Xác nhận thanh toán");
        btnThanhToan.setToolTipText("Thanh toán cho hóa đơn đang mở (nếu có vé được thêm)");
        btnThanhToan.addActionListener(e -> xacNhanThanhToan());
        pnlTimKhachHang.add(Box.createHorizontalStrut(10));
        pnlTimKhachHang.add(btnThanhToan);

        pnlTop.add(pnlTimKhachHang, BorderLayout.CENTER);

        // Train search panel
        JPanel pnlTimChuyenTau = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTimChuyenTau.setBorder(BorderFactory.createTitledBorder("Tìm chuyến tàu"));

        pnlTimChuyenTau.add(new JLabel("Ga đi:"));
        cmbGaDi = new JComboBox<>();
        cmbGaDi.setPreferredSize(new Dimension(120, 25));
        pnlTimChuyenTau.add(cmbGaDi);

        pnlTimChuyenTau.add(new JLabel("Ga đến:"));
        cmbGaDen = new JComboBox<>();
        cmbGaDen.setPreferredSize(new Dimension(120, 25));
        pnlTimChuyenTau.add(cmbGaDen);

        pnlTimChuyenTau.add(new JLabel("Ngày đi:"));
        dateNgayDi = new JDateChooser();
        dateNgayDi.setPreferredSize(new Dimension(120, 25));
        dateNgayDi.setDateFormatString("dd/MM/yyyy");
        pnlTimChuyenTau.add(dateNgayDi);

        pnlTimChuyenTau.add(new JLabel("Giờ đi (từ):"));
        SpinnerDateModel timeModel = new SpinnerDateModel();
        spnGioDi = new JSpinner(timeModel);
        JSpinner.DateEditor timeEditor = new JSpinner.DateEditor(spnGioDi, "HH:mm");
        spnGioDi.setEditor(timeEditor);
        spnGioDi.setPreferredSize(new Dimension(80, 25));
        pnlTimChuyenTau.add(spnGioDi);

        btnTimChuyenTau = new JButton("Tìm chuyến tàu");
        btnTimChuyenTau.addActionListener(e -> timChuyenTau());
        pnlTimChuyenTau.add(btnTimChuyenTau);

        pnlTop.add(pnlTimChuyenTau, BorderLayout.SOUTH);
        add(pnlTop, BorderLayout.NORTH);

        // Main content panel
        JPanel pnlNoiDung = new JPanel(new BorderLayout(10, 10));

        // Top section - Train results table
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
        scrollChuyenTau.setPreferredSize(new Dimension(0, 150));
        pnlNoiDung.add(scrollChuyenTau, BorderLayout.NORTH);

        // Bottom section - Carriage and seat selection
        JPanel pnlDuoi = new JPanel(new BorderLayout(10, 10));

        // Left panel - Carriage table
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

        // Right panel - Seat map
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
                    // Clear previous list and try to populate with ChiTietHoaDon records if DAO has method (optional)
                    danhSachVeTrongHoaDon.clear();
                    // We leave danhSachVeTrongHoaDon empty; entries will be added as user adds vé in this session.
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
        // Get search criteria, treating empty strings as null (no filter)
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

        // Search trains with flexible criteria
        List<ChuyenTau> ketQua = chuyenTauDAO.timKiemChuyenTau(gaDi, gaDen, ngayDi, gioDi);

        // Display results
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
        if (heldVeMap.containsKey(ghe.getMaGhe())) {
            // hiển thị as 'Đã giữ' (orange)
            btnGhe.setBackground(Color.BLUE); // blue for held
            btnGhe.setEnabled(false);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Đang giữ (chưa thanh toán)");
            return btnGhe;
        }
        return btnGhe;
    }

    private void chonGhe(Ghe ghe) {
        gheDuocChon = ghe;

        // Validate selections
        if (khachHangDuocChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm và chọn khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        String loaiVeStr = (String) cboLoaiVe.getSelectedItem();

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

    private void datVe(KhachHang khachHang, LoaiVe loaiVe) {
        try {
            // 1) Tạo mã vé tạm (maVe tạm dùng timestamp)
            String maVe = "VE" + System.currentTimeMillis();

            // 2) Tạo đối tượng Ve nhưng KHÔNG gọi veService.taoVe() ở đây
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

            // 3) Kiểm tra xem ghế đã được giữ trong session này chưa
            if (heldVeMap.containsKey(ve.getMaSoGhe())) {
                JOptionPane.showMessageDialog(this, "Ghế này đã được giữ trong hoá đơn hiện tại.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            Ghe gheDB = gheDAO.findById(ve.getMaSoGhe());
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


            // 4) Nếu chưa có hoaDonMo thì tìm hoặc tạo (giống logic trước đó)
            HoaDonDAO hoaDonDAO = HoaDonDAO.getInstance();
            if (hoaDonMo == null) {
                List<HoaDon> danhSachHoaDon = hoaDonDAO.findByKhachHang(khachHang.getMaKhachHang());
                for (HoaDon hd : danhSachHoaDon) {
                    if ("Chờ xác nhận".equals(hd.getTrangThai())) {
                        hoaDonMo = hd;
                        break;
                    }
                }
            }
            if (hoaDonMo == null) {
                String maHoaDon = "HD" + System.currentTimeMillis();
                hoaDonMo = new HoaDon();
                hoaDonMo.setMaHoaDon(maHoaDon);
                hoaDonMo.setMaNV(taiKhoanHienTai != null ? taiKhoanHienTai.getMaNV() : "NV001");
                hoaDonMo.setMaKH(khachHang.getMaKhachHang());
                hoaDonMo.setNgayLap(null);
                hoaDonMo.setPhuongThucThanhToan(null);
                hoaDonMo.setTrangThai("Chờ xác nhận");
                hoaDonDAO.insert(hoaDonMo); // lưu hoá đơn tạm
            }

            // 5) Thêm ve vào cấu trúc tạm trong memory (không chèn ChiTietHoaDon)
            heldVeMap.put(ve.getMaSoGhe(), ve);
            danhSachVeTrongHoaDon.add(ve);

            // 6) Cập nhật UI: đổi màu nút ghế thành 'Đã giữ' (chỉ trên UI)
            // Bạn cần tìm nút tương ứng và đổi màu. Giả sử bạn giữ mapping giữa MaGhe -> JButton (không có sẵn),
            // tạm cách đơn giản: cập nhật gheDuocChon object và gọi repaint layout:
            gheDuocChon.setTrangThai("Đã giữ"); // local object
            hienThiSoDoGhe(toaDuocChon.getMaToa()); // reload hiển thị để thấy đổi màu (nếu code hiển thị dựa trên ghe.getTrangThai())

            JOptionPane.showMessageDialog(this,
                    "Đã thêm vé vào hoá đơn tạm. Bạn có thể tiếp tục chọn vé khác hoặc xác nhận thanh toán.",
                    "Đã thêm", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi thêm vé tạm: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void xacNhanThanhToan() {
        if (hoaDonMo == null || danhSachVeTrongHoaDon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không có vé nào trong hóa đơn để thanh toán.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String phuongThuc = JOptionPane.showInputDialog(this, "Nhập phương thức thanh toán (ví dụ: Tiền mặt):", "Tiền mặt");
        if (phuongThuc == null) return;

        // Gọi checkout; checkout giờ đã được cập nhật để INSERT Ve nếu chưa tồn tại
        boolean ok = HoaDonService.getInstance().checkout(hoaDonMo, new ArrayList<>(danhSachVeTrongHoaDon), phuongThuc);
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