package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.DauMayDAO;
import com.trainstation.dao.GaDAO;
import com.trainstation.dao.ToaTauDAO;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.DauMay;
import com.trainstation.model.Ga;
import com.trainstation.model.ToaTau;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Panel tìm kiếm với 4 tab:
 *  - Chuyến tàu: lọc theo ga đi, ga đến, đầu máy, trạng thái, khoảng ngày (giờ đi)
 *  - Đầu máy: lọc theo mã, loại, trạng thái
 *  - Ga: lọc theo mã, tên, tình trạng
 *  - Toa tàu: lọc theo mã, loại, trạng thái, năm SX, sức chứa
 *
 * Tất cả tìm kiếm thực hiện bằng cách lấy danh sách từ DAO rồi lọc tại tầng ứng dụng (in-memory).
 * Các truy vấn chạy trong SwingWorker để không block EDT.
 */
public class PnlTimKiemChuyenTau extends JPanel {
    private final ChuyenTauDAO chuyenTauDAO = ChuyenTauDAO.getInstance();
    private final DauMayDAO dauMayDAO = DauMayDAO.getInstance();
    private final GaDAO gaDAO = GaDAO.getInstance();
    private final ToaTauDAO toaTauDAO = ToaTauDAO.getInstance();

    // Tab components - ChuyenTau
    private final JComboBox<String> cbFilterGaDi = new JComboBox<>();
    private final JComboBox<String> cbFilterGaDen = new JComboBox<>();
    private final JComboBox<String> cbFilterDauMay = new JComboBox<>();
    private final JComboBox<String> cbFilterTrangThai = new JComboBox<>();
    private final JSpinner spDateFrom;
    private final JSpinner spDateTo;
    private final DefaultTableModel modelChuyen;
    private final JTable tblChuyen;

    // Tab components - DauMay
    private final JTextField txtFilterDauMayMa = new JTextField(10);
    private final JTextField txtFilterDauMayLoai = new JTextField(10);
    private final JComboBox<String> cbFilterDauMayTrangThai = new JComboBox<>(new String[]{"", "Sẵn sàng", "Bảo trì", "Tạm dừng", "Dừng hoạt động"});
    private final DefaultTableModel modelDauMay;
    private final JTable tblDauMay;

    // Tab components - Ga
    private final JTextField txtFilterGaMa = new JTextField(10);
    private final JTextField txtFilterGaTen = new JTextField(10);
    private final JComboBox<String> cbFilterGaTinhTrang = new JComboBox<>(new String[]{"", "Hoạt động", "Bảo trì", "Tạm dừng"});
    private final DefaultTableModel modelGa;
    private final JTable tblGa;

    // Tab components - ToaTau
    private final JTextField txtFilterToaMa = new JTextField(10);
    private final JTextField txtFilterToaLoai = new JTextField(10);
    private final JTextField txtFilterToaNamSX = new JTextField(10);
    private final JTextField txtFilterToaSucChua = new JTextField(10);
    private final JComboBox<String> cbFilterToaTrangThai = new JComboBox<>(new String[]{"", "Hoạt động", "Bảo trì", "Ngưng sử dụng"});
    private final DefaultTableModel modelToa;
    private final JTable tblToa;

    public PnlTimKiemChuyenTau() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JTabbedPane tabs = new JTabbedPane();

        // --- Chuyến tàu tab ---
        JPanel pnlChuyen = new JPanel(new BorderLayout(8,8));
        JPanel filterChuyen = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6,6,6,6);
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;

        // populate combos
        cbFilterGaDi.setEditable(true);
        cbFilterGaDen.setEditable(true);
        cbFilterDauMay.setEditable(true);
        cbFilterTrangThai.setEditable(true);

        refreshGaFilters();
        refreshDauMayFilter();
        cbFilterTrangThai.addItem("");
        cbFilterTrangThai.addItem("Chưa khởi hành");
        cbFilterTrangThai.addItem("Đã khởi hành");
        cbFilterTrangThai.addItem("Đã đến");
        cbFilterTrangThai.addItem("Hủy");

        // date spinners
        spDateFrom = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spDateFrom.setEditor(new JSpinner.DateEditor(spDateFrom, "dd/MM/yyyy"));
        spDateTo = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spDateTo.setEditor(new JSpinner.DateEditor(spDateTo, "dd/MM/yyyy"));

        int r = 0;
        gc.gridx = 0; gc.gridy = r; filterChuyen.add(new JLabel("Ga đi (mã):"), gc);
        gc.gridx = 1; filterChuyen.add(cbFilterGaDi, gc);
        gc.gridx = 2; filterChuyen.add(new JLabel("Ga đến (mã):"), gc);
        gc.gridx = 3; filterChuyen.add(cbFilterGaDen, gc);
        r++;
        gc.gridx = 0; gc.gridy = r; filterChuyen.add(new JLabel("Đầu máy:"), gc);
        gc.gridx = 1; filterChuyen.add(cbFilterDauMay, gc);
        gc.gridx = 2; filterChuyen.add(new JLabel("Trạng thái:"), gc);
        gc.gridx = 3; filterChuyen.add(cbFilterTrangThai, gc);
        r++;
        gc.gridx = 0; gc.gridy = r; filterChuyen.add(new JLabel("Ngày đi từ:"), gc);
        gc.gridx = 1; filterChuyen.add(spDateFrom, gc);
        gc.gridx = 2; filterChuyen.add(new JLabel("đến:"), gc);
        gc.gridx = 3; filterChuyen.add(spDateTo, gc);
        r++;

        JPanel btnsChuyen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnSearchChuyen = new JButton("Tìm");
        JButton btnResetChuyen = new JButton("Đặt lại");
        MaterialInitializer.styleButton(btnSearchChuyen);
        MaterialInitializer.styleButton(btnResetChuyen);
        btnsChuyen.add(btnSearchChuyen);
        btnsChuyen.add(btnResetChuyen);

        pnlChuyen.add(filterChuyen, BorderLayout.NORTH);
        pnlChuyen.add(btnsChuyen, BorderLayout.SOUTH);

        modelChuyen = new DefaultTableModel(new String[]{"Mã chuyến","Đầu máy","Ga đi","Ga đến","Giờ đi","Giờ đến","Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblChuyen = new JTable(modelChuyen);
        tblChuyen.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spChuyen = new JScrollPane(tblChuyen);
        pnlChuyen.add(spChuyen, BorderLayout.CENTER);

        // --- DauMay tab ---
        JPanel pnlDauMay = new JPanel(new BorderLayout(8,8));
        JPanel filterDauMay = new JPanel(new GridBagLayout());
        GridBagConstraints gd = new GridBagConstraints();
        gd.insets = new Insets(6,6,6,6);
        gd.anchor = GridBagConstraints.WEST;
        gd.fill = GridBagConstraints.HORIZONTAL;

        gd.gridx = 0; gd.gridy = 0; filterDauMay.add(new JLabel("Mã:"), gd);
        gd.gridx = 1; filterDauMay.add(txtFilterDauMayMa, gd);
        gd.gridx = 2; filterDauMay.add(new JLabel("Loại:"), gd);
        gd.gridx = 3; filterDauMay.add(txtFilterDauMayLoai, gd);

        gd.gridx = 0; gd.gridy = 1; filterDauMay.add(new JLabel("Trạng thái:"), gd);
        gd.gridx = 1; filterDauMay.add(cbFilterDauMayTrangThai, gd);

        JPanel btnsDauMay = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnSearchDauMay = new JButton("Tìm");
        JButton btnResetDauMay = new JButton("Đặt lại");
        MaterialInitializer.styleButton(btnSearchDauMay);
        MaterialInitializer.styleButton(btnResetDauMay);
        btnsDauMay.add(btnSearchDauMay);
        btnsDauMay.add(btnResetDauMay);

        pnlDauMay.add(filterDauMay, BorderLayout.NORTH);
        pnlDauMay.add(btnsDauMay, BorderLayout.SOUTH);

        modelDauMay = new DefaultTableModel(new String[]{"Mã đầu máy","Loại","Tên","Năm SX","Lần bảo trì gần nhất","Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblDauMay = new JTable(modelDauMay);
        tblDauMay.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spDauMay = new JScrollPane(tblDauMay);
        pnlDauMay.add(spDauMay, BorderLayout.CENTER);

        // --- Ga tab ---
        JPanel pnlGa = new JPanel(new BorderLayout(8,8));
        JPanel filterGa = new JPanel(new GridBagLayout());
        GridBagConstraints gg = new GridBagConstraints();
        gg.insets = new Insets(6,6,6,6);
        gg.fill = GridBagConstraints.HORIZONTAL;
        gg.anchor = GridBagConstraints.WEST;

        gg.gridx = 0; gg.gridy = 0; filterGa.add(new JLabel("Mã ga:"), gg);
        gg.gridx = 1; filterGa.add(txtFilterGaMa, gg);
        gg.gridx = 2; filterGa.add(new JLabel("Tên ga:"), gg);
        gg.gridx = 3; filterGa.add(txtFilterGaTen, gg);

        gg.gridx = 0; gg.gridy = 1; filterGa.add(new JLabel("Tình trạng:"), gg);
        gg.gridx = 1; filterGa.add(cbFilterGaTinhTrang, gg);

        JPanel btnsGa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnSearchGa = new JButton("Tìm");
        JButton btnResetGa = new JButton("Đặt lại");
        MaterialInitializer.styleButton(btnSearchGa);
        MaterialInitializer.styleButton(btnResetGa);
        btnsGa.add(btnSearchGa);
        btnsGa.add(btnResetGa);

        pnlGa.add(filterGa, BorderLayout.NORTH);
        pnlGa.add(btnsGa, BorderLayout.SOUTH);

        modelGa = new DefaultTableModel(new String[]{"Mã ga","Tên ga","Mô tả","Tình trạng","Địa chỉ"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblGa = new JTable(modelGa);
        tblGa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spGa = new JScrollPane(tblGa);
        pnlGa.add(spGa, BorderLayout.CENTER);

        // --- Toa Tàu tab ---
        JPanel pnlToa = new JPanel(new BorderLayout(8,8));
        JPanel filterToa = new JPanel(new GridBagLayout());
        GridBagConstraints gt = new GridBagConstraints();
        gt.insets = new Insets(6,6,6,6);
        gt.fill = GridBagConstraints.HORIZONTAL;
        gt.anchor = GridBagConstraints.WEST;

        // Hàng 0: Mã toa - Loại toa
        gt.gridx = 0; gt.gridy = 0; filterToa.add(new JLabel("Mã toa:"), gt);
        gt.gridx = 1; filterToa.add(txtFilterToaMa, gt);
        gt.gridx = 2; filterToa.add(new JLabel("Loại toa:"), gt);
        gt.gridx = 3; filterToa.add(txtFilterToaLoai, gt);

        // Hàng 1: Năm SX - Trạng thái
        gt.gridx = 0; gt.gridy = 1; filterToa.add(new JLabel("Năm SX:"), gt);
        gt.gridx = 1; filterToa.add(txtFilterToaNamSX, gt);
        gt.gridx = 2; filterToa.add(new JLabel("Trạng thái:"), gt);
        gt.gridx = 3; filterToa.add(cbFilterToaTrangThai, gt);

        // Hàng 2: Sức chứa
        gt.gridx = 0; gt.gridy = 2; filterToa.add(new JLabel("Sức chứa ≥"), gt);
        gt.gridx = 1; filterToa.add(txtFilterToaSucChua, gt);

        JPanel btnsToa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnSearchToa = new JButton("Tìm");
        JButton btnResetToa = new JButton("Đặt lại");
        MaterialInitializer.styleButton(btnSearchToa);
        MaterialInitializer.styleButton(btnResetToa);
        btnsToa.add(btnSearchToa);
        btnsToa.add(btnResetToa);

        pnlToa.add(filterToa, BorderLayout.NORTH);
        pnlToa.add(btnsToa, BorderLayout.SOUTH);

        modelToa = new DefaultTableModel(new String[]{"Mã toa","Loại toa","Năm SX","Trạng thái","Sức chứa"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tblToa = new JTable(modelToa);
        tblToa.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spToa = new JScrollPane(tblToa);
        pnlToa.add(spToa, BorderLayout.CENTER);

        // add tabs
        tabs.addTab("Tìm chuyến", pnlChuyen);
        tabs.addTab("Tìm đầu máy", pnlDauMay);
        tabs.addTab("Tìm ga", pnlGa);
        tabs.addTab("Tìm toa tàu", pnlToa);

        add(tabs, BorderLayout.CENTER);

        // wire actions
        btnSearchChuyen.addActionListener(e -> searchChuyen());
        btnResetChuyen.addActionListener(e -> {
            cbFilterGaDi.setSelectedItem("");
            cbFilterGaDen.setSelectedItem("");
            cbFilterDauMay.setSelectedItem("");
            cbFilterTrangThai.setSelectedItem("");
            spDateFrom.setValue(new Date());
            spDateTo.setValue(new Date());
            modelChuyen.setRowCount(0);
        });

        btnSearchDauMay.addActionListener(e -> searchDauMay());
        btnResetDauMay.addActionListener(e -> {
            txtFilterDauMayMa.setText("");
            txtFilterDauMayLoai.setText("");
            cbFilterDauMayTrangThai.setSelectedItem("");
            modelDauMay.setRowCount(0);
        });

        btnSearchGa.addActionListener(e -> searchGa());
        btnResetGa.addActionListener(e -> {
            txtFilterGaMa.setText("");
            txtFilterGaTen.setText("");
            cbFilterGaTinhTrang.setSelectedItem("");
            modelGa.setRowCount(0);
        });

        btnSearchToa.addActionListener(e -> searchToa());
        btnResetToa.addActionListener(e -> {
            txtFilterToaMa.setText("");
            txtFilterToaLoai.setText("");
            txtFilterToaNamSX.setText("");
            txtFilterToaSucChua.setText("");
            cbFilterToaTrangThai.setSelectedItem("");
            modelToa.setRowCount(0);
        });

        // initial populate small lists
        loadAllDauMayTable(); // optional show all
        loadAllGaTable();
        loadAllToaTable();
        try {
            UIUtils.adjustTableForScale(tblChuyen, 1.1f);
            UIUtils.adjustTableForScale(tblDauMay, 1.1f);
            UIUtils.adjustTableForScale(tblGa, 1.1f);
            UIUtils.adjustTableForScale(tblToa, 1.1f);
        } catch (Throwable ignored) {}
    }

    private void refreshGaFilters() {
        cbFilterGaDi.removeAllItems();
        cbFilterGaDi.addItem("");
        cbFilterGaDen.removeAllItems();
        cbFilterGaDen.addItem("");
        try {
            List<Ga> list = gaDAO.getAll();
            if (list != null) {
                for (Ga g : list) {
                    if (g != null && g.getMaGa() != null) {
                        cbFilterGaDi.addItem(g.getMaGa());
                        cbFilterGaDen.addItem(g.getMaGa());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshDauMayFilter() {
        cbFilterDauMay.removeAllItems();
        cbFilterDauMay.addItem("");
        try {
            List<DauMay> list = dauMayDAO.getAll();
            if (list != null) {
                for (DauMay d : list) if (d != null && d.getMaDauMay() != null) cbFilterDauMay.addItem(d.getMaDauMay());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // --- search implementations ---

    private void searchChuyen() {
        final String gaDi = nonEmpty(cbFilterGaDi.getSelectedItem());
        final String gaDen = nonEmpty(cbFilterGaDen.getSelectedItem());
        final String dauMay = nonEmpty(cbFilterDauMay.getSelectedItem());
        final String trangThai = nonEmpty(cbFilterTrangThai.getSelectedItem());
        final Date fromDate = (Date) spDateFrom.getValue();
        final Date toDate = (Date) spDateTo.getValue();

        modelChuyen.setRowCount(0);

        SwingWorker<List<ChuyenTau>, Void> w = new SwingWorker<>() {
            @Override
            protected List<ChuyenTau> doInBackground() {
                List<ChuyenTau> list = chuyenTauDAO.getAll();
                if (list == null) return Collections.emptyList();
                List<ChuyenTau> out = new ArrayList<>();
                LocalDate from = toLocalDateOrNull(fromDate);
                LocalDate to = toLocalDateOrNull(toDate);
                for (ChuyenTau c : list) {
                    if (c == null) continue;
                    if (!gaDi.isEmpty() && !gaDi.equalsIgnoreCase(nullSafe(c.getMaGaDi()))) continue;
                    if (!gaDen.isEmpty() && !gaDen.equalsIgnoreCase(nullSafe(c.getMaGaDen()))) continue;
                    if (!dauMay.isEmpty() && !dauMay.equalsIgnoreCase(nullSafe(c.getMaDauMay()))) continue;
                    if (!trangThai.isEmpty()) {
                        String t = nullSafe(c.getTrangThai());
                        if (t == null || !t.toLowerCase().contains(trangThai.toLowerCase())) continue;
                    }
                    if (from != null || to != null) {
                        LocalDateTime gd = c.getGioDi();
                        if (gd == null) continue; // skip templates when date filter applied
                        LocalDate d = gd.toLocalDate();
                        if (from != null && d.isBefore(from)) continue;
                        if (to != null && d.isAfter(to)) continue;
                    }
                    out.add(c);
                }
                return out;
            }

            @Override
            protected void done() {
                try {
                    List<ChuyenTau> res = get();
                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    for (ChuyenTau c : res) {
                        String gd = c.getGioDi() != null ? c.getGioDi().format(fmt) : "";
                        String den = c.getGioDen() != null ? c.getGioDen().format(fmt) : "";
                        modelChuyen.addRow(new Object[]{ c.getMaChuyen(), c.getMaDauMay(), c.getMaGaDi(), c.getMaGaDen(), gd, den, c.getTrangThai() });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemChuyenTau.this, "Lỗi khi tìm chuyến: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void searchDauMay() {
        final String ma = txtFilterDauMayMa.getText().trim();
        final String loai = txtFilterDauMayLoai.getText().trim();
        final String trangThai = nonEmpty(cbFilterDauMayTrangThai.getSelectedItem());

        modelDauMay.setRowCount(0);

        SwingWorker<List<DauMay>, Void> w = new SwingWorker<>() {
            @Override
            protected List<DauMay> doInBackground() {
                List<DauMay> all = dauMayDAO.getAll();
                if (all == null) return Collections.emptyList();
                List<DauMay> out = new ArrayList<>();
                for (DauMay d : all) {
                    if (d == null) continue;
                    if (!ma.isEmpty() && (d.getMaDauMay() == null || !d.getMaDauMay().toLowerCase().contains(ma.toLowerCase()))) continue;
                    if (!loai.isEmpty() && (d.getLoaiDauMay() == null || !d.getLoaiDauMay().toLowerCase().contains(loai.toLowerCase()))) continue;
                    if (!trangThai.isEmpty() && (d.getTrangThai() == null || !d.getTrangThai().equalsIgnoreCase(trangThai))) continue;
                    out.add(d);
                }
                return out;
            }

            @Override
            protected void done() {
                try {
                    List<DauMay> res = get();
                    for (DauMay d : res) {
                        Object lt = d.getLanBaoTriGanNhat();
                        modelDauMay.addRow(new Object[]{ d.getMaDauMay(), d.getLoaiDauMay(), d.getTenDauMay(), d.getNamSX(), lt, d.getTrangThai() });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemChuyenTau.this, "Lỗi khi tìm đầu máy: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void searchGa() {
        final String ma = txtFilterGaMa.getText().trim();
        final String ten = txtFilterGaTen.getText().trim();
        final String tinhTrang = nonEmpty(cbFilterGaTinhTrang.getSelectedItem());

        modelGa.setRowCount(0);

        SwingWorker<List<Ga>, Void> w = new SwingWorker<>() {
            @Override
            protected List<Ga> doInBackground() {
                List<Ga> all = gaDAO.getAll();
                if (all == null) return Collections.emptyList();
                List<Ga> out = new ArrayList<>();
                for (Ga g : all) {
                    if (g == null) continue;
                    if (!ma.isEmpty() && (g.getMaGa() == null || !g.getMaGa().toLowerCase().contains(ma.toLowerCase()))) continue;
                    if (!ten.isEmpty() && (g.getTenGa() == null || !g.getTenGa().toLowerCase().contains(ten.toLowerCase()))) continue;
                    if (!tinhTrang.isEmpty() && (g.getTinhTrang() == null || !g.getTinhTrang().equalsIgnoreCase(tinhTrang))) continue;
                    out.add(g);
                }
                return out;
            }

            @Override
            protected void done() {
                try {
                    List<Ga> res = get();
                    for (Ga g : res) {
                        modelGa.addRow(new Object[]{ g.getMaGa(), g.getTenGa(), g.getMoTa(), g.getTinhTrang(), g.getDiaChi() });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemChuyenTau.this, "Lỗi khi tìm ga: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void searchToa() {
        final String ma = txtFilterToaMa.getText().trim();
        final String loai = txtFilterToaLoai.getText().trim();
        final String namSXStr = txtFilterToaNamSX.getText().trim();
        final String trangThai = nonEmpty(cbFilterToaTrangThai.getSelectedItem());
        final String sucChuaStr = txtFilterToaSucChua.getText().trim();

        modelToa.setRowCount(0);

        SwingWorker<List<ToaTau>, Void> w = new SwingWorker<>() {
            @Override
            protected List<ToaTau> doInBackground() {
                List<ToaTau> all = toaTauDAO.getAll();
                if (all == null) return Collections.emptyList();
                List<ToaTau> out = new ArrayList<>();

                Integer namSXFilter = null;
                Integer sucChuaFilter = null;
                try {
                    if (!namSXStr.isEmpty()) namSXFilter = Integer.parseInt(namSXStr);
                } catch (NumberFormatException ignored) {}
                try {
                    if (!sucChuaStr.isEmpty()) sucChuaFilter = Integer.parseInt(sucChuaStr);
                } catch (NumberFormatException ignored) {}

                for (ToaTau t : all) {
                    if (t == null) continue;
                    if (!ma.isEmpty() && (t.getMaToa() == null || !t.getMaToa().toLowerCase().contains(ma.toLowerCase()))) continue;
                    if (!loai.isEmpty() && (t.getLoaiToa() == null || !t.getLoaiToa().toLowerCase().contains(loai.toLowerCase()))) continue;
                    if (!trangThai.isEmpty() && (t.getTrangThai() == null || !t.getTrangThai().equalsIgnoreCase(trangThai))) continue;
                    if (namSXFilter != null) {
                        if (t.getSamSX() == null || !t.getSamSX().equals(namSXFilter)) continue;
                    }
                    if (sucChuaFilter != null) {
                        if (t.getSucChua() == null || t.getSucChua() < sucChuaFilter) continue;
                    }
                    out.add(t);
                }
                return out;
            }

            @Override
            protected void done() {
                try {
                    List<ToaTau> res = get();
                    for (ToaTau t : res) {
                        modelToa.addRow(new Object[]{
                                t.getMaToa(),
                                t.getLoaiToa(),
                                t.getSamSX(),
                                t.getTrangThai(),
                                t.getSucChua()
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemChuyenTau.this, "Lỗi khi tìm toa tàu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    // small helpers
    private static String nonEmpty(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private static String nullSafe(String s) {
        return s == null ? "" : s;
    }

    private static LocalDate toLocalDateOrNull(Date d) {
        if (d == null) return null;
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    // optional convenience loaders
    private void loadAllDauMayTable() {
        modelDauMay.setRowCount(0);
        List<DauMay> list = dauMayDAO.getAll();
        if (list == null) return;
        for (DauMay d : list) {
            modelDauMay.addRow(new Object[]{ d.getMaDauMay(), d.getLoaiDauMay(), d.getTenDauMay(), d.getNamSX(), d.getLanBaoTriGanNhat(), d.getTrangThai() });
        }
    }

    private void loadAllGaTable() {
        modelGa.setRowCount(0);
        List<Ga> list = gaDAO.getAll();
        if (list == null) return;
        for (Ga g : list) {
            modelGa.addRow(new Object[]{ g.getMaGa(), g.getTenGa(), g.getMoTa(), g.getTinhTrang(), g.getDiaChi() });
        }
    }

    private void loadAllToaTable() {
        modelToa.setRowCount(0);
        List<ToaTau> list = toaTauDAO.getAll();
        if (list == null) return;
        for (ToaTau t : list) {
            modelToa.addRow(new Object[]{ t.getMaToa(), t.getLoaiToa(), t.getSamSX(), t.getTrangThai(), t.getSucChua() });
        }
    }
}