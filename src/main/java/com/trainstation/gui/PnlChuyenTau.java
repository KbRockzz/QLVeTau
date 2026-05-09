package com.trainstation.gui;

import com.trainstation.MySQL.ConnectSql;
import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.*;
import com.trainstation.model.*;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

/**
 * PnlChuyenTau - Panel quản lý chuyến tàu.
 *
 * Thay đổi chính:
 *  - Khi mở dialog "Thêm Toa", combobox chỉ chứa các mã toa khả dụng vào khoảng thời gian của chuyến (không xung đột).
 *  - Khi thực hiện thêm, dùng MySQL GET_LOCK("toa_<maToa>") + kiểm tra overlap trong cùng connection để tránh race condition.
 *
 * LƯU Ý:
 *  - Nếu chuyến chưa có thời gian (template), combobox chứa tất cả mã toa hiện có trong ChiTietChuyenTau.
 *  - Các thông báo lỗi/khóa timeout được hiển thị cho người dùng.
 */
public class PnlChuyenTau extends JPanel {
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Regex pattern for validation
    private static final Pattern PATTERN_MA_CHUYEN = Pattern.compile("^CT\\w+");
    
    // Route type constants
    private static final String CH_NGAN = "CH_NGAN";
    private static final String CH_TRUNG = "CH_TRUNG";
    private static final String CH_DAI = "CH_DAI";

    // DAOs
    private final ChuyenTauDAO chuyenTauDAO = ChuyenTauDAO.getInstance();
    private final VeDAO veDAO = VeDAO.getInstance();
    private final GaDAO gaDAO = GaDAO.getInstance();
    private final DauMayDAO dauMayDAO = DauMayDAO.getInstance();
    private final ChiTietChuyenTauDAO ctctDAO = ChiTietChuyenTauDAO.getInstance();
    private final NhanVienDAO nhanVienDAO = NhanVienDAO.getInstance();
    private final ChangTauDAO changTauDAO = ChangTauDAO.getInstance();

    // UI components - main list
    private final JTable table;
    private final DefaultTableModel model;

    // Form fields for ChuyenTau
    private final JTextField txtMaChuyen;
    private final JComboBox<String> cbMaTau;     // đầu máy (maDauMay) - editable
    private final JComboBox<String> cbGaDi;      // ga đi - editable
    private final JComboBox<String> cbGaDen;     // ga đến - editable
    private final JComboBox<String> cbNhanVien;  // mã nhân viên (maNV) - editable
    private final JComboBox<String> cbMaChang;   // mã chặng - editable
    private final JComboBox<String> cbTrangThai; // trạng thái - editable
    private final JSpinner spinnerGioDi;
    private final JSpinner spinnerGioDen;
    private final JSpinner dateSpinner;

    // Buttons for ChuyenTau actions
    private final JButton btnRefresh, btnAdd, btnUpdate, btnDelete;
//    ,btnStart, btnArrived;

    // Composition (Chi tiet chuyen - Toa) UI
    private final JTable tblComposition;
    private final DefaultTableModel compositionModel;
    private final JButton btnCompRefresh, btnCompAdd, btnCompRemove, btnCompUp, btnCompDown, btnCompSaveOrder;
    // add near other fields
    private final JLabel lblInfo;                 // hiển thị tóm tắt thông tin chuyến
    private List<String> lastAvailableToa = null; // cache danh sách toa khả dụng cho chuyến đang chọn

    private ScheduledExecutorService scheduler;
    private ToaTauDAO toaTauDAO = ToaTauDAO.getInstance();

    public PnlChuyenTau() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // --- Top bar: ngày + refresh ---
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Chọn ngày:"));
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        topBar.add(dateSpinner);

        btnRefresh = new JButton("Làm mới");
        MaterialInitializer.styleButton(btnRefresh);
        btnRefresh.addActionListener(e -> loadData());
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);

        // --- Main table (ChuyenTau) ---
        model = new DefaultTableModel(new String[]{"Mã chuyến", "Mã đầu máy", "Ga đi", "Ga đến", "Ngày giờ chạy", "Số vé", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến"));
        MaterialInitializer.setTableScrollPaneSize(sp, 35);

        // --- Form (inline) for ChuyenTau ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến (inline)"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        int row = 0;
        txtMaChuyen = new JTextField(15);

        // cbMaTau: populate from DauMayDAO; editable so user can type custom code
        cbMaTau = new JComboBox<>();
        cbMaTau.setEditable(true);
        refreshDauMayCombo();

        // cbGaDi / cbGaDen: populate from GaDAO; editable
        cbGaDi = new JComboBox<>();
        cbGaDi.setEditable(true);
        cbGaDen = new JComboBox<>();
        cbGaDen.setEditable(true);
        refreshGaCombos();

        // cbNhanVien
        cbNhanVien = new JComboBox<>();
        cbNhanVien.setEditable(true);
        refreshNhanVienCombo();

        // cbMaChang
        cbMaChang = new JComboBox<>();
        cbMaChang.setEditable(true);
        refreshChangCombo();

        // cbTrangThai - predefined choices (editable)
        cbTrangThai = new JComboBox<>(new String[] {"Chưa khởi hành","Đã khởi hành","Đã đến","Hủy"});
        cbTrangThai.setEditable(true);

        SpinnerDateModel dtModelDi = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        spinnerGioDi = new JSpinner(dtModelDi);
        spinnerGioDi.setEditor(new JSpinner.DateEditor(spinnerGioDi, "dd/MM/yyyy HH:mm"));

        SpinnerDateModel dtModelDen = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        spinnerGioDen = new JSpinner(dtModelDen);
        spinnerGioDen.setEditor(new JSpinner.DateEditor(spinnerGioDen, "dd/MM/yyyy HH:mm"));

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Mã chuyến:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(txtMaChuyen, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Mã đầu máy:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(cbMaTau, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Mã nhân viên:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(cbNhanVien, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Mã chặng:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(cbMaChang, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ga đi (mã):"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(cbGaDi, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ga đến (mã):"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(cbGaDen, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ngày giờ chạy:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(spinnerGioDi, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ngày giờ đến dự kiến:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(spinnerGioDen, c);

        c.gridwidth = 1;

        // trạng thái
        c.gridx = 0; c.gridy = ++row; c.weightx = 0.0; form.add(new JLabel("Trạng thái:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(cbTrangThai, c);

        // Buttons for CRUD on ChuyenTau
        JPanel formBtns = MaterialInitializer.createButtonPanel();
        formBtns.setLayout(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
//        btnStart = new JButton("Khởi hành");
//        btnArrived = new JButton("Đến nơi");
        MaterialInitializer.styleButton(btnAdd);
        MaterialInitializer.styleButton(btnUpdate);
        MaterialInitializer.styleButton(btnDelete);
//        MaterialInitializer.styleButton(btnStart);
//        MaterialInitializer.styleButton(btnArrived);
        formBtns.add(btnAdd); formBtns.add(btnUpdate); formBtns.add(btnDelete);
//        formBtns.add(btnStart); formBtns.add(btnArrived);

        c.gridx = 0; c.gridy = ++row; c.gridwidth = 2; form.add(formBtns, c);
        c.gridwidth = 1;

        // --- Composition panel: Chi tiết chuyến (Toa) ---
        JPanel compPanel = new JPanel(new BorderLayout(6,6));
        compPanel.setBorder(BorderFactory.createTitledBorder("Chi tiết chuyến (Toa)"));

        compositionModel = new DefaultTableModel(new String[]{"Mã Toa", "Số thứ tự", "Sức chứa"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblComposition = new JTable(compositionModel);
        tblComposition.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spComp = new JScrollPane(tblComposition);
        spComp.setPreferredSize(new Dimension(360, 180));
        compPanel.add(spComp, BorderLayout.CENTER);

        // composition controls
        JPanel compControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 6));
        btnCompRefresh = new JButton("Làm mới");
        btnCompAdd = new JButton("Thêm Toa");
        btnCompRemove = new JButton("Xóa Toa");
        btnCompUp = new JButton("Lên");
        btnCompDown = new JButton("Xuống");
        btnCompSaveOrder = new JButton("Lưu thứ tự");
        MaterialInitializer.styleButton(btnCompRefresh);
        MaterialInitializer.styleButton(btnCompAdd);
        MaterialInitializer.styleButton(btnCompRemove);
        MaterialInitializer.styleButton(btnCompUp);
        MaterialInitializer.styleButton(btnCompDown);
        MaterialInitializer.styleButton(btnCompSaveOrder);
        compControls.add(btnCompRefresh);
        compControls.add(btnCompAdd);
        compControls.add(btnCompRemove);
        compControls.add(btnCompUp);
        compControls.add(btnCompDown);
        compControls.add(btnCompSaveOrder);
        compPanel.add(compControls, BorderLayout.SOUTH);

        // Put composition panel below form (stacked vertically)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(form, BorderLayout.NORTH);
        rightPanel.add(compPanel, BorderLayout.CENTER);
        JScrollPane rightScroll = new JScrollPane(rightPanel);
        rightScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        rightScroll.getVerticalScrollBar().setUnitIncrement(16);

        // Split pane: left = main list, right = form + composition
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, rightScroll);
        split.setResizeWeight(0.65);
        add(split, BorderLayout.CENTER);

        // Bottom info
        lblInfo = new JLabel(""); // khởi tạo
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JLabel("Chọn 1 chuyến để sửa hoặc thao tác. Thêm/chỉnh sửa sẽ cập nhật DB trực tiếp."), BorderLayout.WEST);
        bottom.add(lblInfo, BorderLayout.EAST); // hiển thị tóm tắt trạng thái
        add(bottom, BorderLayout.SOUTH);

        // --- Listeners ---
        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) populateFormFromSelection();
        });

        btnAdd.addActionListener(e -> addChuyen());
        btnUpdate.addActionListener(e -> updateChuyen());
        btnDelete.addActionListener(e -> deleteChuyen());
//        btnStart.addActionListener(e -> startSelected());
//        btnArrived.addActionListener(e -> arrivedSelected());

        btnCompRefresh.addActionListener(e -> loadCompositionForSelected());
        btnCompAdd.addActionListener(e -> showAddToaDialog());
        btnCompRemove.addActionListener(e -> removeSelectedToa());
        btnCompUp.addActionListener(e -> moveSelectedToaUp());
        btnCompDown.addActionListener(e -> moveSelectedToaDown());
        btnCompSaveOrder.addActionListener(e -> saveCompositionOrder());

        try { UIUtils.adjustTableForScale(table, 1.2f); } catch (Throwable ignored) {}

        startAutoStarter();
        loadData();
    }


    private void showAddToaDialog() {
        String maChuyen = getSelectedMaChuyenFromTable();
        if (maChuyen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        ChuyenTau chuy = chuyenTauDAO.findById(maChuyen);
        if (chuy == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- XÁC ĐỊNH LOẠI CHẶNG TỪ maChang / soKm ---
        ChangTau chang = null;
        try {
            String maChang = chuy.getMaChang();
            if (maChang != null && !maChang.trim().isEmpty()) {
                chang = changTauDAO.findById(maChang);
            }
        } catch (Exception ignored) {}

        boolean isShortOrMedium = false; // true = chặng ngắn hoặc trung, false = chặng dài
        Integer soKm = chuy.getSoKm();
        if (soKm != null) {
            // <200km = ngắn, 200–600 = trung, >600 = dài
            if (soKm < 200 || soKm <= 600) {
                isShortOrMedium = true;
            }
        } else if (chang != null) {
            Integer maxKm = chang.getSoKMToiDa();
            if (maxKm != null && maxKm <= 600) {
                isShortOrMedium = true;
            }
        }

        JComboBox<String> comboMaToa = new JComboBox<>();
        comboMaToa.setEditable(true);

        // --- LẤY DANH SÁCH TOA KHẢ DỤNG THEO THỜI GIAN ---
        List<String> available = lastAvailableToa != null ? lastAvailableToa : getAvailableToaForChuyen(maChuyen);
        if (available == null) available = Collections.emptyList();

        // Nếu không có danh sách “toa đang rảnh theo thời gian” -> fallback sang TẤT CẢ toa đang active
        if (available.isEmpty()) {
            try {
                List<ToaTau> allToa = toaTauDAO.getAll(); // lấy trực tiếp từ bảng ToaTau
                List<String> tmp = new ArrayList<>();
                if (allToa != null) {
                    for (ToaTau t : allToa) {
                        if (t != null && t.getMaToa() != null) {
                            tmp.add(t.getMaToa());
                        }
                    }
                }
                available = tmp;
            } catch (Exception ex) {
                ex.printStackTrace();
                available = Collections.emptyList();
            }
        }

        // --- LỌC THEO LOẠI CHẶNG / LOẠI TOA ---
        List<String> filtered = new ArrayList<>();
        for (String maToa : available) {
            if (maToa == null) continue;
            ToaTau toa = toaTauDAO.findById(maToa);
            if (toa == null) continue;

            String loaiToa = toa.getLoaiToa();
            if (loaiToa == null) loaiToa = "";

            // Trong DB: loaiToa IN ('TOANGOI', 'TOANAM')
            // Chặng ngắn & trung: KHÔNG cho TOANAM
            if (isShortOrMedium && "TOANAM".equalsIgnoreCase(loaiToa)) {
                continue; // bỏ toa nằm
            }

            filtered.add(maToa);
        }

        if (filtered.isEmpty()) {
            String msg;
            if (isShortOrMedium) {
                msg = "Chặng này là chặng ngắn/trung.\n" +
                        "Không có toa ghế (TOANGOI) khả dụng trong khung giờ này hoặc trong danh sách toa.";
            } else {
                msg = "Không có toa khả dụng trong khung giờ này hoặc trong danh sách toa.";
            }
            JOptionPane.showMessageDialog(this, msg, "Không có toa phù hợp", JOptionPane.WARNING_MESSAGE);
            return;
        }

        for (String s : filtered) comboMaToa.addItem(s);

        JTextField fldSucChua = new JTextField(6);

        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; p.add(new JLabel("Mã Toa:"), gbc);
        gbc.gridx = 1; p.add(comboMaToa, gbc);

        gbc.gridx = 0; gbc.gridy = 1; p.add(new JLabel("Sức chứa (số):"), gbc);
        gbc.gridx = 1; p.add(fldSucChua, gbc);

        int rc = JOptionPane.showConfirmDialog(this, p, "Thêm Toa vào chuyến " + maChuyen,
                JOptionPane.OK_CANCEL_OPTION);
        if (rc != JOptionPane.OK_OPTION) return;

        Object selToa = comboMaToa.getSelectedItem();
        String maToa = selToa != null ? selToa.toString().trim() : "";
        if (maToa.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã toa không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Kiểm tra lại loại toa (trường hợp người dùng gõ tay)
        ToaTau toaChon = toaTauDAO.findById(maToa);
        if (toaChon == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin toa: " + maToa, "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String loaiToaChon = toaChon.getLoaiToa() != null ? toaChon.getLoaiToa().trim() : "";
        if (isShortOrMedium && "TOANAM".equalsIgnoreCase(loaiToaChon)) {
            JOptionPane.showMessageDialog(this,
                    "Chặng này là chặng ngắn/trung nên không được thêm toa nằm (TOANAM).\n" +
                            "Vui lòng chọn toa ghế (TOANGOI).",
                    "Không hợp lệ",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra lại toa còn khả dụng theo thời gian
        List<String> currentAvailable = getAvailableToaForChuyen(maChuyen);
        if (currentAvailable != null && !currentAvailable.isEmpty() && !currentAvailable.contains(maToa)) {
            JOptionPane.showMessageDialog(this,
                    "Toa " + maToa + " hiện đang được sử dụng bởi chuyến khác trong cùng khung giờ.\n" +
                            "Vui lòng chọn toa khác hoặc điều chỉnh thời gian chuyến.",
                    "Toa đang bận",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Integer suc = null;
        try {
            String s = fldSucChua.getText().trim();
            if (!s.isEmpty()) suc = Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Sức chứa phải là số.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        final Integer finalSuc = suc;
        final String finalMaToa = maToa;

        btnCompAdd.setEnabled(false);

        SwingWorker<String, Void> w = new SwingWorker<>() {
            @Override
            protected String doInBackground() {
                try {
                    // Thêm bản ghi mới với soThuTuToa = 0 (hoặc null)
                    ChiTietChuyenTau newT = new ChiTietChuyenTau(maChuyen, finalMaToa, 0, finalSuc);
                    boolean added = ctctDAO.add(newT);
                    if (!added) return "ERROR";

                    // Sau khi thêm, reindex lại 1..n trong DB
                    ctctDAO.reindexSoThuTuToa(maChuyen);
                    return "OK";
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return "ERROR";
                }
            }

            @Override
            protected void done() {
                btnCompAdd.setEnabled(true);
                try {
                    String result = get();
                    if (!"OK".equals(result)) {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this,
                                "Đã có lỗi khi thêm toa. Xem log.",
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    JOptionPane.showMessageDialog(PnlChuyenTau.this,
                            "Đã thêm toa.",
                            "Kết quả", JOptionPane.INFORMATION_MESSAGE);

                    // Load lại từ DB để số thứ tự luôn chuẩn
                    loadCompositionForChuyen(maChuyen);

                    // Cập nhật cache toa khả dụng
                    lastAvailableToa = getAvailableToaForChuyen(maChuyen);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlChuyenTau.this,
                            "Lỗi khi xử lý kết quả.",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }


    private void startAutoStarter() {
        // nếu đã tạo rồi thì bỏ qua
        if (scheduler != null && !scheduler.isShutdown()) return;

        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ChuyenTau-AutoStarter");
            t.setDaemon(true);
            return t;
        });

        // initial delay 15s, run every 60s
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkAndAutoStart();
            } catch (Throwable t) {
                // log để biết lỗi nhưng không phá scheduler
                t.printStackTrace();
            }
        }, 15, 60, TimeUnit.SECONDS);
    }

    /**
     * Kiểm tra danh sách chuyến và gọi chuyenTauDAO.startChuyenOnDate(...) cho những chuyến
     * có gioDi không null, thuộc ngày hôm nay và thời gian chạy <= hiện tại, và chưa có trạng thái "khởi hành".
     *
     * - Hàm này đọc toàn bộ chuyến (chuyenTauDAO.getAll()) rồi lọc theo điều kiện.
     * - Sau khi cập nhật trạng thái thành công với DAO, sẽ gọi SwingUtilities.invokeLater(this::loadData)
     *   để refresh UI (chạy trên EDT).
     */
    private void checkAndAutoStart() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<ChuyenTau> list = chuyenTauDAO.getAll();
        if (list == null) return;

        for (ChuyenTau c : list) {
            try {
                LocalDateTime gioDi = c.getGioDi();
                if (gioDi == null) continue; // template không tự start
                if (!gioDi.toLocalDate().equals(today)) continue; // chỉ quan tâm chuyến hôm nay

                String status = c.getTrangThai();
                boolean alreadyStarted = status != null && status.toLowerCase().contains("khởi hành");
                if (alreadyStarted) continue;

                // nếu giờ đi <= giờ hiện tại => khởi hành
                if (!gioDi.isAfter(now)) {
                    boolean ok = chuyenTauDAO.startChuyenOnDate(c.getMaChuyen(), today, "AUTO");
                    if (ok) {
                        // cập nhật UI trên EDT
                        SwingUtilities.invokeLater(this::loadData);
                    }
                }
            } catch (Exception ex) {
                // log lỗi chuyến đó rồi tiếp tục
                ex.printStackTrace();
            }
        }
    }

    /**
     * Đảm bảo gọi khi panel đóng để shutdown scheduler.
     * (Bạn đã có phương thức dispose(); đảm bảo nó gọi scheduler.shutdownNow())
     */
    @Override
    public void removeNotify() {
        // optional: khi panel bị remove khỏi container, dọn scheduler
        try {
            if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();
        } catch (Throwable ignored) {}
        super.removeNotify();
    }
    private void refreshDauMayCombo() {
        cbMaTau.removeAllItems();
        try {
            List<DauMay> list = dauMayDAO.getAll();
            if (list != null) {
                for (DauMay d : list) {
                    if (d != null && d.getMaDauMay() != null) cbMaTau.addItem(d.getMaDauMay());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshGaCombos() {
        cbGaDi.removeAllItems();
        cbGaDen.removeAllItems();
        try {
            List<Ga> list = gaDAO.getAll();
            if (list != null) {
                for (Ga g : list) {
                    if (g != null && g.getMaGa() != null) {
                        cbGaDi.addItem(g.getMaGa());
                        cbGaDen.addItem(g.getMaGa());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshNhanVienCombo() {
        cbNhanVien.removeAllItems();
        try {
            List<NhanVien> list = nhanVienDAO.getAll();
            if (list != null) {
                for (NhanVien n : list) {
                    if (n != null && n.getMaNV() != null) cbNhanVien.addItem(n.getMaNV());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshChangCombo() {
        cbMaChang.removeAllItems();
        try {
            List<ChangTau> list = changTauDAO.getAll();
            if (list != null) {
                for (ChangTau ch : list) {
                    if (ch != null && ch.getMaChang() != null) cbMaChang.addItem(ch.getMaChang());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Return list of available toa codes for the given chuyến's time window.
     * If chuyến has no time (gioDi or gioDen null) => return all known toa codes (from ChiTietChuyenTau).
     *
     * Excludes toa that are assigned to a different active chuyến that overlaps the given [start,end).
     */
    private List<String> getAvailableToaForChuyen(String maChuyen) {
        List<String> result = new ArrayList<>();

        // Load target chuyến
        ChuyenTau target = chuyenTauDAO.findById(maChuyen);
        if (target == null) return result;

        LocalDateTime start = target.getGioDi();
        LocalDateTime end   = target.getGioDen();

        // --- 1. Lấy TẤT CẢ toa từ bảng ToaTau ---
        List<ToaTau> allToaEntities = toaTauDAO.getAll(); // đã filter isActive trong DAO
        if (allToaEntities == null) allToaEntities = Collections.emptyList();

        // Nếu chuyến chưa có khung thời gian -> tất cả toa đều coi là khả dụng
        if (start == null || end == null) {
            for (ToaTau t : allToaEntities) {
                if (t != null && t.getMaToa() != null) {
                    result.add(t.getMaToa());
                }
            }
            return result;
        }

        // --- 2. Load tất cả gán toa cho các chuyến khác ---
        List<ChiTietChuyenTau> allAssignments = ctctDAO.getAll();
        if (allAssignments == null) allAssignments = Collections.emptyList();

        // Map: maChuyen -> set(maToa) đang dùng
        Map<String, Set<String>> chuyenToToas = new HashMap<>();
        for (ChiTietChuyenTau a : allAssignments) {
            if (a == null) continue;
            String mc = a.getMaChuyenTau();
            String mt = a.getMaToaTau();
            if (mc == null || mt == null) continue;
            chuyenToToas.computeIfAbsent(mc, k -> new HashSet<>()).add(mt);
        }

        // --- 3. Load tất cả chuyến để kiểm tra khung giờ ---
        List<ChuyenTau> allChuyens = chuyenTauDAO.getAll();
        if (allChuyens == null) allChuyens = Collections.emptyList();

        // Helper overlap: [start,end) với [oStart,oEnd)
        BiPredicate<LocalDateTime, LocalDateTime> overlapsWithTarget = (oStart, oEnd) -> {
            if (oStart == null || oEnd == null) return false;
            // oEnd <= start OR oStart >= end  => không overlap
            if (oEnd.isBefore(start) || oEnd.isEqual(start)) return false;
            if (oStart.isAfter(end) || oStart.isEqual(end)) return false;
            return true;
        };

        // --- 4. Với MỖI toa trong ToaTau, kiểm tra có bị “bận” bởi chuyến khác không ---
        for (ToaTau toa : allToaEntities) {
            if (toa == null || toa.getMaToa() == null) continue;
            String maToa = toa.getMaToa();
            boolean available = true;

            for (ChuyenTau other : allChuyens) {
                if (other == null) continue;
                String otherMa = other.getMaChuyen();
                if (maChuyen.equals(otherMa)) continue; // bỏ qua chính mình

                Set<String> otherToas = chuyenToToas.get(otherMa);
                if (otherToas == null || !otherToas.contains(maToa)) continue; // chuyến kia không dùng toa này

                LocalDateTime oStart = other.getGioDi();
                LocalDateTime oEnd   = other.getGioDen();
                if (overlapsWithTarget.test(oStart, oEnd)) {
                    available = false;
                    break;
                }
            }

            if (available) result.add(maToa);
        }

        return result;
    }


    private void loadCompositionForSelected() {
        String ma = getSelectedMaChuyenFromTable();
        if (ma == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        loadCompositionForChuyen(ma);
    }

    private void loadCompositionForChuyen(String maChuyen) {
        btnCompRefresh.setEnabled(false);
        compositionModel.setRowCount(0);
        SwingWorker<Void, Object[]> w = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                List<ChiTietChuyenTau> list = ctctDAO.findByChuyenTau(maChuyen);
                if (list == null) return null;
                for (ChiTietChuyenTau t : list) {
                    publish(new Object[]{ t.getMaToaTau(), t.getSoThuTuToa(), t.getSucChua() });
                }
                return null;
            }
            @Override protected void process(List<Object[]> chunks) {
                for (Object[] r : chunks) compositionModel.addRow(r);
            }
            @Override protected void done() {
                btnCompRefresh.setEnabled(true);
            }
        };
        w.execute();
    }


    private void removeSelectedToa() {
        String maChuyen = getSelectedMaChuyenFromTable();
        if (maChuyen == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int r = tblComposition.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn toa để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maToa = (String) compositionModel.getValueAt(r, 0);
        Integer stt = (Integer) compositionModel.getValueAt(r, 1);
        int conf = JOptionPane.showConfirmDialog(this,
                "Xác nhận xóa toa " + maToa + " (thứ tự " + stt + ")?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        btnCompRemove.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                boolean ok = ctctDAO.delete(maChuyen, maToa);
                if (!ok) return false;
                ctctDAO.reindexSoThuTuToa(maChuyen);
                return true;
            }

            @Override
            protected void done() {
                btnCompRemove.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã xóa toa.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        loadCompositionForChuyen(maChuyen);              // reload model
                        lastAvailableToa = getAvailableToaForChuyen(maChuyen);
                    } else {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể xóa toa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void moveSelectedToaUp() { moveSelectedToa(-1); }
    private void moveSelectedToaDown() { moveSelectedToa(1); }

    private void moveSelectedToa(int direction) {
        String maChuyen = getSelectedMaChuyenFromTable();
        if (maChuyen == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        int r = tblComposition.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn toa để di chuyển.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        int targetRow = r + direction;
        if (targetRow < 0 || targetRow >= compositionModel.getRowCount()) return;

        String maToaA = (String) compositionModel.getValueAt(r, 0);
        String maToaB = (String) compositionModel.getValueAt(targetRow, 0);

        btnCompUp.setEnabled(false); btnCompDown.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                ChiTietChuyenTau a = ctctDAO.findById(maChuyen, maToaA);
                ChiTietChuyenTau b = ctctDAO.findById(maChuyen, maToaB);
                if (a == null || b == null) return false;
                Integer tmp = a.getSoThuTuToa();
                a.setSoThuTuToa(b.getSoThuTuToa());
                b.setSoThuTuToa(tmp);
                boolean ok1 = ctctDAO.update(a);
                boolean ok2 = ctctDAO.update(b);
                return ok1 && ok2;
            }
            @Override
            protected void done() {
                btnCompUp.setEnabled(true); btnCompDown.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) {
                        loadCompositionForChuyen(maChuyen);
                        // Đảm bảo set selection sau khi table đã reload xong
                        SwingUtilities.invokeLater(() -> {
                            int rowCount = compositionModel.getRowCount();
                            if (rowCount == 0) return;
                            int newRow = targetRow;
                            if (newRow < 0) newRow = 0;
                            if (newRow >= rowCount) newRow = rowCount - 1;
                            tblComposition.getSelectionModel().setSelectionInterval(newRow, newRow);
                        });
                    } else {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this,
                                "Không thể di chuyển toa.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void saveCompositionOrder() {
        String maChuyen = getSelectedMaChuyenFromTable();
        if (maChuyen == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến trước.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        // read order from table rows: set soThuTu based on current row index (1-based)
        btnCompSaveOrder.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                int rows = compositionModel.getRowCount();
                for (int i = 0; i < rows; i++) {
                    String maToa = (String) compositionModel.getValueAt(i, 0);
                    ChiTietChuyenTau t = ctctDAO.findById(maChuyen, maToa);
                    if (t == null) continue;
                    t.setSoThuTuToa(i + 1);
                    ctctDAO.update(t);
                }
                return true;
            }
            @Override protected void done() {
                btnCompSaveOrder.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) { JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã lưu thứ tự.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); loadCompositionForChuyen(maChuyen); }
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    // ---------- ChuyenTau list / form operations (unchanged) ----------

    private LocalDate getSelectedDate() {
        Date d = (Date) dateSpinner.getValue();
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void loadData() {
        btnRefresh.setEnabled(false);
        model.setRowCount(0);
        LocalDate date = getSelectedDate();

        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override protected Void doInBackground() {
                List<ChuyenTau> list = chuyenTauDAO.getAll();
                if (list == null) return null;
                for (ChuyenTau c : list) {
                    try {
                        boolean include = false;
                        LocalDateTime runTime = c.getGioDi();
                        if (runTime != null) include = runTime.toLocalDate().equals(date);
                        else {
                            int cnt = chuyenTauDAO.countTicketsForChuyenOnDate(c.getMaChuyen());
                            include = cnt > 0;
                        }
                        if (!include) continue;
                        int count = chuyenTauDAO.countTicketsForChuyenOnDate(c.getMaChuyen());
                        String runTimeStr = runTime != null ? runTime.format(DT_FMT) : "";
                        String status = c.getTrangThai() != null ? c.getTrangThai() : "";
                        String tenGaDi = layTenGa(c.getMaGaDi());
                        String tenGaDen = layTenGa(c.getMaGaDen());

                        publish(new Object[]{ c.getMaChuyen(), c.getMaDauMay(), tenGaDi, tenGaDen, runTimeStr, count, status });
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
                return null;
            }

            @Override protected void process(List<Object[]> chunks) { for (Object[] r : chunks) model.addRow(r); }

            @Override protected void done() {
                btnRefresh.setEnabled(true);
                try { UIUtils.adjustTableForScale(table, 1.2f); } catch (Throwable ignored) {}
            }
        };
        worker.execute();
    }

    private void populateFormFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0) {
            clearForm();
            compositionModel.setRowCount(0);
            lastAvailableToa = null;
            lblInfo.setText("");
            setCompositionControlsEnabled(false);
            return;
        }
        String ma = (String) model.getValueAt(r, 0);
        if (ma == null) return;

        setCompositionControlsEnabled(false);
        SwingWorker<ChuyenTau, Void> w = new SwingWorker<>() {
            private List<ChiTietChuyenTau> composition;

            @Override
            protected ChuyenTau doInBackground() {
                // load chuyến và composition, ticket count, available toa
                ChuyenTau ct = chuyenTauDAO.findById(ma);
                try {
                    composition = ctctDAO.findByChuyenTau(ma);
                } catch (Exception ex) {
                    composition = Collections.emptyList();
                }
                return ct;
            }

            @Override
            protected void done() {
                try {
                    ChuyenTau ct = get();
                    if (ct == null) return;

                    // populate basic fields
                    txtMaChuyen.setText(ct.getMaChuyen());
                    txtMaChuyen.setEnabled(false);

                    cbMaTau.setSelectedItem(ct.getMaDauMay() != null ? ct.getMaDauMay() : "");
                    cbNhanVien.setSelectedItem(ct.getMaNV() != null ? ct.getMaNV() : "");
                    cbMaChang.setSelectedItem(ct.getMaChang() != null ? ct.getMaChang() : "");
                    cbGaDi.setSelectedItem(ct.getMaGaDi() != null ? ct.getMaGaDi() : "");
                    cbGaDen.setSelectedItem(ct.getMaGaDen() != null ? ct.getMaGaDen() : "");

                    // set tooltips for ga combos (show tên ga nếu có)
                    try {
                        if (ct.getMaGaDi() != null) {
                            Ga g = gaDAO.findById(ct.getMaGaDi());
                            if (g != null && g.getTenGa() != null) cbGaDi.setToolTipText(g.getTenGa());
                            else cbGaDi.setToolTipText(null);
                        } else cbGaDi.setToolTipText(null);

                        if (ct.getMaGaDen() != null) {
                            Ga g2 = gaDAO.findById(ct.getMaGaDen());
                            if (g2 != null && g2.getTenGa() != null) cbGaDen.setToolTipText(g2.getTenGa());
                            else cbGaDen.setToolTipText(null);
                        } else cbGaDen.setToolTipText(null);
                    } catch (Exception ignored) {}

                    // giờ đi / đến
                    if (ct.getGioDi() != null) {
                        Date d = Date.from(ct.getGioDi().atZone(ZoneId.systemDefault()).toInstant());
                        spinnerGioDi.setValue(d);
                        spinnerGioDi.setEnabled(true);
                    } else {
                        spinnerGioDi.setEnabled(false);
                    }
                    if (ct.getGioDen() != null) {
                        Date d2 = Date.from(ct.getGioDen().atZone(ZoneId.systemDefault()).toInstant());
                        spinnerGioDen.setValue(d2);
                        spinnerGioDen.setEnabled(true);
                    } else {
                        spinnerGioDen.setEnabled(false);
                    }

                    cbTrangThai.setSelectedItem(ct.getTrangThai() != null ? ct.getTrangThai() : "");

                    // load composition into table
                    compositionModel.setRowCount(0);
                    int totalCapacity = 0;
                    if (composition != null) {
                        for (ChiTietChuyenTau t : composition) {
                            int suc = t.getSucChua() != null ? t.getSucChua() : 0;
                            totalCapacity += suc;
                            compositionModel.addRow(new Object[]{ t.getMaToaTau(), t.getSoThuTuToa(), suc });
                        }
                    }

                    // ticket count for selected date
                    int ticketsToday = 0;
                    try {
                        ticketsToday = chuyenTauDAO.countTicketsForChuyenOnDate(ct.getMaChuyen());
                    } catch (Exception ignored) {}

                    // set info label
                    String info = String.format("Toa: %d   Tổng sức chứa: %d   Vé (%s): %d",
                            composition != null ? composition.size() : 0,
                            totalCapacity,
                            getSelectedDate().toString(),
                            ticketsToday);
                    lblInfo.setText(info);

                    // cache available toa for add dialog (so add dialog can use cached list)
                    try {
                        lastAvailableToa = getAvailableToaForChuyen(ct.getMaChuyen());
                    } catch (Exception ex) {
                        lastAvailableToa = null;
                    }

                    setCompositionControlsEnabled(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    // helper to enable/disable composition-related buttons
    private void setCompositionControlsEnabled(boolean enabled) {
        btnCompRefresh.setEnabled(enabled);
        btnCompAdd.setEnabled(enabled);
        btnCompRemove.setEnabled(enabled);
        btnCompUp.setEnabled(enabled);
        btnCompDown.setEnabled(enabled);
        btnCompSaveOrder.setEnabled(enabled);
    }


    private void clearForm() {
        txtMaChuyen.setText("");
        txtMaChuyen.setEnabled(true);
        cbMaTau.setSelectedItem("");
        cbNhanVien.setSelectedItem("");
        cbMaChang.setSelectedItem("");
        cbGaDi.setSelectedItem("");
        cbGaDen.setSelectedItem("");
        spinnerGioDi.setValue(new Date());
        spinnerGioDen.setValue(new Date());
        spinnerGioDi.setEnabled(true);
        spinnerGioDen.setEnabled(true);
        cbTrangThai.setSelectedItem("");
        compositionModel.setRowCount(0);
    }

    private String layTenGa(String maGa) {
        if (maGa == null || maGa.trim().isEmpty()) return "N/A";
        try {
            Ga g = gaDAO.findById(maGa);
            return g != null ? g.getTenGa() : maGa;
        } catch (Exception e) {
            return maGa;
        }
    }

    private ChuyenTau buildChuyenFromForm() {
        ChuyenTau ct = new ChuyenTau();
        ct.setMaChuyen(txtMaChuyen.getText().trim());

        Object dauMaySel = cbMaTau.getSelectedItem();
        ct.setMaDauMay(dauMaySel != null ? dauMaySel.toString().trim() : null);

        Object nvSel = cbNhanVien.getSelectedItem();
        ct.setMaNV(nvSel != null ? nvSel.toString().trim() : null);

        Object changSel = cbMaChang.getSelectedItem();
        String maChang = changSel != null ? changSel.toString().trim() : null;
        ct.setMaChang(maChang);

        Object gaDiSel = cbGaDi.getSelectedItem();
        ct.setMaGaDi(gaDiSel != null ? gaDiSel.toString().trim() : null);

        Object gaDenSel = cbGaDen.getSelectedItem();
        ct.setMaGaDen(gaDenSel != null ? gaDenSel.toString().trim() : null);

        Date d = (Date) spinnerGioDi.getValue();
        if (d != null) ct.setGioDi(Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());
        Date d2 = (Date) spinnerGioDen.getValue();
        if (d2 != null) ct.setGioDen(Instant.ofEpochMilli(d2.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime());

        Object statusSel = cbTrangThai.getSelectedItem();
        ct.setTrangThai(statusSel != null ? statusSel.toString().trim() : null);

        // Set default soKM based on maChang only if soKM is not already set
        if (ct.getSoKm() == null) {
            ct.setSoKm(getDefaultSoKm(maChang));
        }

        return ct;
    }

    /**
     * Get default soKM value based on maChang (route type).
     * Returns 150 for short routes (CH_NGAN), 400 for medium routes (CH_TRUNG),
     * and 1500 for long routes (CH_DAI).
     */
    private Integer getDefaultSoKm(String maChang) {
        if (maChang == null) {
            return null;
        }
        
        switch (maChang) {
            case CH_NGAN:
                return 150;
            case CH_TRUNG:
                return 400;
            case CH_DAI:
                return 1500;
            default:
                return null;
        }
    }

    private String getSelectedMaChuyenFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        Object o = model.getValueAt(r, 0);
        return o != null ? o.toString() : null;
    }

    private void addChuyen() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) { JOptionPane.showMessageDialog(this, "Mã chuyến không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
        
        // Validate maChuyen starts with CT
        if (!PATTERN_MA_CHUYEN.matcher(ma).matches()) {
            JOptionPane.showMessageDialog(this, "Mã chuyến phải bắt đầu bằng 'CT' (ví dụ: CT001, CT_HN_SG).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChuyenTau ct = buildChuyenFromForm();

        btnAdd.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() { return chuyenTauDAO.insert(ct); }
            @Override protected void done() {
                btnAdd.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) { JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã thêm chuyến.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); loadData(); clearForm(); }
                    else JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể thêm chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void updateChuyen() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc nhập mã chuyến.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        
        // Validate maChuyen starts with CT
        if (!PATTERN_MA_CHUYEN.matcher(ma).matches()) {
            JOptionPane.showMessageDialog(this, "Mã chuyến phải bắt đầu bằng 'CT' (ví dụ: CT001, CT_HN_SG).", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        ChuyenTau ct = buildChuyenFromForm();

        btnUpdate.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() { return chuyenTauDAO.update(ct); }
            @Override protected void done() {
                btnUpdate.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) { JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã cập nhật chuyến.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); loadData(); }
                    else JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể cập nhật chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void deleteChuyen() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) { JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        int conf = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chuyến " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        btnDelete.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() { return chuyenTauDAO.delete(ma); }
            @Override protected void done() {
                btnDelete.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) { JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã xóa.", "Kết quả", JOptionPane.INFORMATION_MESSAGE); loadData(); clearForm(); }
                    else JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể xóa chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

//    private void startSelected() {
//        String ma = getSelectedMaChuyenFromTable();
//        if (ma == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để khởi hành.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
//        LocalDate date = getSelectedDate();
//        int cf = JOptionPane.showConfirmDialog(this, "Xác nhận đánh dấu chuyến " + ma + " ngày " + date + " là 'Đã khởi hành'?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//        if (cf != JOptionPane.YES_OPTION) return;
//        btnStart.setEnabled(false);
//        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
//            @Override protected Boolean doInBackground() { return chuyenTauDAO.startChuyenOnDate(ma, date, "UI"); }
//            @Override protected void done() {
//                btnStart.setEnabled(true);
//                try {
//                    boolean ok = get();
//                    JOptionPane.showMessageDialog(PnlChuyenTau.this, ok ? "Đã cập nhật: Khởi hành." : "Không thể cập nhật trạng thái.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
//                    loadData();
//                } catch (Exception ex) { ex.printStackTrace(); }
//            }
//        };
//        w.execute();
//    }

//    private void arrivedSelected() {
//        String ma = getSelectedMaChuyenFromTable();
//        if (ma == null) { JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để đánh dấu đến nơi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
//        LocalDate date = getSelectedDate();
//        String[] options = {"Chỉ giải phóng vé chưa thanh toán", "Giải phóng cả vé đã thanh toán", "Hủy"};
//        int choice = JOptionPane.showOptionDialog(this,
//                "Chọn chính sách giải phóng ghế cho chuyến " + ma + " ngày " + date + ":",
//                "Chính sách giải phóng ghế",
//                JOptionPane.DEFAULT_OPTION,
//                JOptionPane.QUESTION_MESSAGE,
//                null,
//                options,
//                options[0]);
//        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;
//        boolean freePaid = (choice == 1);
//
//        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đánh dấu chuyến " + ma + " ngày " + date + " là 'Đã đến' và giải phóng ghế?", "Xác nhận", JOptionPane.YES_NO_OPTION);
//        if (confirm != JOptionPane.YES_OPTION) return;
//
//        btnArrived.setEnabled(false);
//        SwingWorker<Integer, Void> w = new SwingWorker<>() {
//            @Override protected Integer doInBackground() { return chuyenTauDAO.arriveChuyenOnDate(ma, date, freePaid, "UI"); }
//            @Override protected void done() {
//                btnArrived.setEnabled(true);
//                try {
//                    int updated = get();
//                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PnlChuyenTau.this,
//                            updated > 0 ? "Đã xử lý - ghế đã được giải phóng: " + updated : "Đã cập nhật trạng thái chuyến, nhưng không có ghế nào thay đổi.",
//                            "Kết quả", JOptionPane.INFORMATION_MESSAGE));
//                    loadData();
//                } catch (Exception ex) { ex.printStackTrace(); }
//            }
//        };
//        w.execute();
//    }

    /**
     * Gọi khi panel/khung chứa panel đóng để shutdown scheduler.
     */
    public void dispose() {
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();
    }
}
