package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.GaDAO;
import com.trainstation.dao.VeDAO;
import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ga;
import com.trainstation.model.Ve;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * PnlTimVe - Panel tìm vé
 *
 * Các chức năng:
 *  - Lọc theo: mã vé, mã chuyến, mã ghế, mã/ tên ga đi, mã/ tên ga đến, trạng thái, khoảng ngày (ngày in)
 *  - Hiển thị kết quả trong bảng; chọn 1 hàng để xem chi tiết; có thể hủy vé (đánh dấu không active / trạng thái = "Đã hủy")
 *
 * Tất cả lọc thực hiện trên dữ liệu lấy về từ VeDAO.getAll() (lọc in-memory).
 */
public class PnlTimVe extends JPanel {
    private final VeDAO veDAO = VeDAO.getInstance();
    private final ChuyenTauDAO chuyenTauDAO = ChuyenTauDAO.getInstance();
    private final GaDAO gaDAO = GaDAO.getInstance();

    // Filter controls
    private final JTextField txtMaVe = new JTextField();
    private final JTextField txtMaChuyen = new JTextField();
    private final JTextField txtMaGhe = new JTextField();
    private final JComboBox<String> cbGaDi = new JComboBox<>();
    private final JComboBox<String> cbGaDen = new JComboBox<>();
    private final JTextField txtTrangThai = new JTextField();
    private final JSpinner spFromDate;
    private final JSpinner spToDate;
    private final JCheckBox chkUseFrom = new JCheckBox("Có từ");
    private final JCheckBox chkUseTo = new JCheckBox("Có đến");

    // Table
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã vé","Mã chuyến","Mã ghế","Ga đi","Ga đến","Ngày in","Trạng thái","Mã bảng giá","Giá"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);

    // Buttons
    private final JButton btnSearch = new JButton("Tìm");
    private final JButton btnReset = new JButton("Đặt lại");
    private final JButton btnView = new JButton("Xem chi tiết");
    private final JButton btnCancel = new JButton("Hủy vé");

    public PnlTimVe() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        // initialize date spinners with non-null Date instances
        spFromDate = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spFromDate.setEditor(new JSpinner.DateEditor(spFromDate, "dd/MM/yyyy"));
        spToDate = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        spToDate.setEditor(new JSpinner.DateEditor(spToDate, "dd/MM/yyyy"));

        initTopPanel();
        initTablePanel();
        initBottomPanel();
        doSearch();

        refreshGaCombos();
        try { UIUtils.adjustTableForScale(table, 1.1f); } catch (Throwable ignored) {}
    }

    private void initTopPanel() {
        JPanel pnlFilters = new JPanel(new GridBagLayout());
        pnlFilters.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm vé"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx = 0; c.gridy = row; pnlFilters.add(new JLabel("Mã vé:"), c);
        c.gridx = 1; c.gridwidth = 1; pnlFilters.add(txtMaVe, c);

        c.gridx = 2; pnlFilters.add(new JLabel("Mã chuyến:"), c);
        c.gridx = 3; pnlFilters.add(txtMaChuyen, c);
        row++;

        c.gridx = 0; c.gridy = row; pnlFilters.add(new JLabel("Mã ghế:"), c);
        c.gridx = 1; pnlFilters.add(txtMaGhe, c);

        c.gridx = 2; pnlFilters.add(new JLabel("Ga đi:"), c);
        c.gridx = 3; pnlFilters.add(cbGaDi, c);
        row++;

        c.gridx = 0; c.gridy = row; pnlFilters.add(new JLabel("Ga đến:"), c);
        c.gridx = 1; pnlFilters.add(cbGaDen, c);

        c.gridx = 2; pnlFilters.add(new JLabel("Trạng thái:"), c);
        c.gridx = 3; pnlFilters.add(txtTrangThai, c);
        row++;

        c.gridx = 0; c.gridy = row; pnlFilters.add(chkUseFrom, c);
        c.gridx = 1; pnlFilters.add(spFromDate, c);
        c.gridx = 2; pnlFilters.add(chkUseTo, c);
        c.gridx = 3; pnlFilters.add(spToDate, c);
        row++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        MaterialInitializer.styleButton(btnSearch);
        MaterialInitializer.styleButton(btnReset);
        btns.add(btnSearch);
        btns.add(btnReset);
        c.gridx = 0; c.gridy = row; c.gridwidth = 4; pnlFilters.add(btns, c);
        c.gridwidth = 1;

        add(pnlFilters, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> doSearch());
        btnReset.addActionListener(e -> resetFilters());
    }

    private void initTablePanel() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Kết quả tìm vé"));
        add(sp, BorderLayout.CENTER);

        // double click to view
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) viewSelectedTicket();
            }
        });
    }

    private void initBottomPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        MaterialInitializer.styleButton(btnView);
        MaterialInitializer.styleButton(btnCancel);
        pnl.add(btnView);
        pnl.add(btnCancel);
        add(pnl, BorderLayout.SOUTH);

        btnView.addActionListener(e -> viewSelectedTicket());
        btnCancel.addActionListener(e -> cancelSelectedTicket());
    }

    private void refreshGaCombos() {
        cbGaDi.removeAllItems(); cbGaDen.removeAllItems();
        cbGaDi.addItem(""); cbGaDen.addItem("");
        try {
            List<Ga> list = gaDAO.getAll();
            if (list != null) {
                for (Ga g : list) {
                    if (g != null && g.getMaGa() != null) {
                        String display = String.format("%s - %s", g.getMaGa(), g.getTenGa() == null ? "" : g.getTenGa());
                        cbGaDi.addItem(display);
                        cbGaDen.addItem(display);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doSearch() {
        final String maVe = txtMaVe.getText().trim();
        final String maChuyen = txtMaChuyen.getText().trim();
        final String maGhe = txtMaGhe.getText().trim();
        final String gaDiSel = nonEmpty(cbGaDi.getSelectedItem());
        final String gaDenSel = nonEmpty(cbGaDen.getSelectedItem());
        final String trangThai = txtTrangThai.getText().trim();
        final Date from = chkUseFrom.isSelected() ? (Date) spFromDate.getValue() : null;
        final Date to = chkUseTo.isSelected() ? (Date) spToDate.getValue() : null;

        model.setRowCount(0);
        btnSearch.setEnabled(false);

        SwingWorker<List<Ve>, Void> w = new SwingWorker<>() {
            @Override
            protected List<Ve> doInBackground() {
                List<Ve> all = veDAO.getAll();
                if (all == null) return Collections.emptyList();
                List<Ve> out = new ArrayList<>();
                LocalDate fromDate = toLocalDateOrNull(from);
                LocalDate toDate = toLocalDateOrNull(to);
                for (Ve v : all) {
                    if (v == null) continue;
                    if (!maVe.isEmpty() && (v.getMaVe() == null || !v.getMaVe().equalsIgnoreCase(maVe))) continue;
                    if (!maChuyen.isEmpty() && (v.getMaChuyen() == null || !v.getMaChuyen().equalsIgnoreCase(maChuyen))) continue;
                    if (!maGhe.isEmpty() && (v.getMaSoGhe() == null || !v.getMaSoGhe().equalsIgnoreCase(maGhe))) continue;
                    if (!trangThai.isEmpty() && (v.getTrangThai() == null || !v.getTrangThai().toLowerCase().contains(trangThai.toLowerCase()))) continue;

                    // ga filters: cb contains "code - name" or empty
                    if (!gaDiSel.isEmpty()) {
                        String code = extractCodeFromCombo(gaDiSel);
                        if (v.getMaGaDi() == null || !v.getMaGaDi().equalsIgnoreCase(code)) continue;
                    }
                    if (!gaDenSel.isEmpty()) {
                        String code = extractCodeFromCombo(gaDenSel);
                        if (v.getMaGaDen() == null || !v.getMaGaDen().equalsIgnoreCase(code)) continue;
                    }

                    if (fromDate != null || toDate != null) {
                        if (v.getNgayIn() == null) continue;
                        LocalDate d = v.getNgayIn().toLocalDate();
                        if (fromDate != null && d.isBefore(fromDate)) continue;
                        if (toDate != null && d.isAfter(toDate)) continue;
                    }

                    out.add(v);
                }
                return out;
            }

            @Override
            protected void done() {
                try {
                    List<Ve> res = get();
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                    for (Ve v : res) {
                        String gaDiName = v.getTenGaDi() != null ? v.getTenGaDi() : v.getMaGaDi();
                        String gaDenName = v.getTenGaDen() != null ? v.getTenGaDen() : v.getMaGaDen();
                        String ngayIn = v.getNgayIn() != null ? v.getNgayIn().format(dtf) : "";
                        String gia = v.getGiaThanhToan() != null ? String.format("%.2f", v.getGiaThanhToan()) : "";
                        model.addRow(new Object[]{
                                v.getMaVe(), v.getMaChuyen(), v.getMaSoGhe(),
                                gaDiName, gaDenName, ngayIn, v.getTrangThai(), v.getMaBangGia(), gia
                        });
                    }
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimVe.this, "Lỗi khi tìm vé: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        };
        w.execute();
    }

    private void resetFilters() {
        txtMaVe.setText("");
        txtMaChuyen.setText("");
        txtMaGhe.setText("");
        cbGaDi.setSelectedIndex(0);
        cbGaDen.setSelectedIndex(0);
        txtTrangThai.setText("");
        chkUseFrom.setSelected(false);
        chkUseTo.setSelected(false);
        spFromDate.setValue(new Date());
        spToDate.setValue(new Date());
        model.setRowCount(0);
    }

    private void viewSelectedTicket() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé để xem.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maVe = (String) model.getValueAt(r, 0);
        Ve v = veDAO.findById(maVe);
        if (v == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy vé chi tiết.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Mã vé: ").append(v.getMaVe()).append("\n");
        sb.append("Mã chuyến: ").append(v.getMaChuyen()).append("\n");
        sb.append("Mã ghế: ").append(v.getMaSoGhe()).append("\n");
        sb.append("Ga đi: ").append(v.getTenGaDi() != null ? v.getTenGaDi() : v.getMaGaDi()).append("\n");
        sb.append("Ga đến: ").append(v.getTenGaDen() != null ? v.getTenGaDen() : v.getMaGaDen()).append("\n");
        sb.append("Ngày in: ").append(v.getNgayIn() != null ? v.getNgayIn().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "").append("\n");
        sb.append("Trạng thái: ").append(v.getTrangThai()).append("\n");
        sb.append("Mã bảng giá: ").append(v.getMaBangGia()).append("\n");
        sb.append("Giá: ").append(v.getGiaThanhToan() != null ? v.getGiaThanhToan() : "").append("\n");

        JOptionPane.showMessageDialog(this, sb.toString(), "Chi tiết vé", JOptionPane.INFORMATION_MESSAGE);
    }

    private void cancelSelectedTicket() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một vé để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String maVe = (String) model.getValueAt(r, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Xác nhận hủy vé " + maVe + " ?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                Ve v = veDAO.findById(maVe);
                if (v == null) return false;
                v.setTrangThai("Đã hủy");
                v.setActive(false);
                return veDAO.update(v);
            }
            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlTimVe.this, "Đã hủy vé.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        doSearch();
                    } else {
                        JOptionPane.showMessageDialog(PnlTimVe.this, "Không thể hủy vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimVe.this, "Lỗi khi hủy vé.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    // helpers
    private static String nonEmpty(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private static LocalDate toLocalDateOrNull(Date d) {
        if (d == null) return null;
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private static String extractCodeFromCombo(String comboItem) {
        // expected formats: "" or "GA001 - Station name"
        if (comboItem == null) return "";
        int idx = comboItem.indexOf(" - ");
        if (idx > 0) return comboItem.substring(0, idx).trim();
        return comboItem.trim();
    }
}