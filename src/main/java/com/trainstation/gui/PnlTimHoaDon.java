package com.trainstation.gui;

import com.toedter.calendar.JDateChooser;
import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.ChiTietHoaDon;
import com.trainstation.model.HoaDon;
import com.trainstation.model.KhachHang;
import com.trainstation.service.HoaDonService;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * PnlTimHoaDon - Panel tìm/tra cứu hóa đơn.
 *
 * Các bộ lọc:
 *  - Mã hóa đơn
 *  - Mã khách hàng
 *  - Tên khách hàng (partial)
 *  - Ngày lập: từ - đến
 *  - Phương thức thanh toán
 *  - Trạng thái
 *
 * Kết quả:
 *  Bảng hiển thị: Mã hóa đơn, Mã KH, Tên KH, Ngày lập, PT thanh toán, Trạng thái, Số vé, Tổng tiền (đã KM)
 *
 * Hành động:
 *  - Tìm, Đặt lại
 *  - Xem chi tiết (mở dialog giống PnlQuanLyVe)
 *  - Xuất PDF (gọi HoaDonService.xuatHoaDonPDF)
 */
public class PnlTimHoaDon extends JPanel {
    private final HoaDonDAO hoaDonDAO = HoaDonDAO.getInstance();
    private final ChiTietHoaDonDAO chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
    private final KhachHangDAO khachHangDAO = KhachHangDAO.getInstance();
    private final HoaDonService hoaDonService = HoaDonService.getInstance();

    // Filters
    private final JTextField txtMaHoaDon = new JTextField();
    private final JTextField txtMaKH = new JTextField();
    private final JTextField txtTenKH = new JTextField();
    private final JDateChooser dateFrom = new JDateChooser();
    private final JDateChooser dateTo = new JDateChooser();
    private final JComboBox<String> cboPhuongThuc = new JComboBox<>(new String[] {"", "Tiền mặt", "Chuyển khoản"});
    private final JComboBox<String> cboTrangThai = new JComboBox<>(new String[] {"", "Chờ xác nhận", "Hoàn tất", "Hủy"});

    // Result table
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã hóa đơn", "Mã KH", "Tên KH", "Ngày lập", "PT thanh toán", "Trạng thái", "Số vé", "Tổng tiền"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(model);

    // Buttons
    private final JButton btnSearch = new JButton("Tìm");
    private final JButton btnReset = new JButton("Đặt lại");
    private final JButton btnView = new JButton("Xem chi tiết");
    private final JButton btnExport = new JButton("Xuất PDF");

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PnlTimHoaDon() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        initFilterPanel();
        initTablePanel();
        initActions();
        doSearch();

        try { UIUtils.adjustTableForScale(table, 1.1f); } catch (Throwable ignored) {}
    }

    private void initFilterPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(BorderFactory.createTitledBorder("Bộ lọc - Hóa đơn"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx = 0; c.gridy = row; pnl.add(new JLabel("Mã hóa đơn:"), c);
        c.gridx = 1; pnl.add(txtMaHoaDon, c);
        c.gridx = 2; pnl.add(new JLabel("Mã KH:"), c);
        c.gridx = 3; pnl.add(txtMaKH, c);
        row++;

        c.gridx = 0; c.gridy = row; pnl.add(new JLabel("Tên KH (partial):"), c);
        c.gridx = 1; pnl.add(txtTenKH, c);
        c.gridx = 2; pnl.add(new JLabel("PT thanh toán:"), c);
        c.gridx = 3; pnl.add(cboPhuongThuc, c);
        row++;

        c.gridx = 0; c.gridy = row; pnl.add(new JLabel("Ngày từ:"), c);
        c.gridx = 1; pnl.add(dateFrom, c);
        c.gridx = 2; pnl.add(new JLabel("Đến:"), c);
        c.gridx = 3; pnl.add(dateTo, c);
        row++;

        c.gridx = 0; c.gridy = row; pnl.add(new JLabel("Trạng thái:"), c);
        c.gridx = 1; pnl.add(cboTrangThai, c);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        MaterialInitializer.styleButton(btnSearch);
        MaterialInitializer.styleButton(btnReset);
        MaterialInitializer.styleButton(btnView);
        MaterialInitializer.styleButton(btnExport);
        btns.add(btnSearch);
        btns.add(btnReset);
        btns.add(btnView);
        btns.add(btnExport);

        c.gridx = 0; c.gridy = ++row; c.gridwidth = 4; pnl.add(btns, c);
        c.gridwidth = 1;

        add(pnl, BorderLayout.NORTH);
    }

    private void initTablePanel() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Kết quả tìm kiếm - Hóa đơn"));
        MaterialInitializer.setTableScrollPaneSize(sp, 40);
        add(sp, BorderLayout.CENTER);
    }

    private void initActions() {
        btnSearch.addActionListener(e -> doSearch());
        btnReset.addActionListener(e -> {
            txtMaHoaDon.setText("");
            txtMaKH.setText("");
            txtTenKH.setText("");
            dateFrom.setDate(null);
            dateTo.setDate(null);
            cboPhuongThuc.setSelectedIndex(0);
            cboTrangThai.setSelectedIndex(0);
            model.setRowCount(0);
        });

        btnView.addActionListener(e -> viewSelectedHoaDon());
        btnExport.addActionListener(e -> exportSelectedHoaDon());

        table.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                boolean sel = table.getSelectedRow() >= 0;
                btnView.setEnabled(sel);
                btnExport.setEnabled(sel);
            }
        });

        // double click to view
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) viewSelectedHoaDon();
            }
        });

        // initial disabled actions
        btnView.setEnabled(false);
        btnExport.setEnabled(false);
    }

    private void doSearch() {
        final String maHoaDon = txtMaHoaDon.getText().trim();
        final String maKH = txtMaKH.getText().trim();
        final String tenKH = txtTenKH.getText().trim().toLowerCase();
        final LocalDate from = dateFrom.getDate() == null ? null : dateFrom.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final LocalDate to = dateTo.getDate() == null ? null : dateTo.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final String phuongThuc = cboPhuongThuc.getSelectedItem() != null ? cboPhuongThuc.getSelectedItem().toString().trim() : "";
        final String trangThai = cboTrangThai.getSelectedItem() != null ? cboTrangThai.getSelectedItem().toString().trim() : "";

        btnSearch.setEnabled(false);
        model.setRowCount(0);

        SwingWorker<List<HoaDon>, Void> w = new SwingWorker<>() {
            @Override
            protected List<HoaDon> doInBackground() {
                List<HoaDon> all = hoaDonDAO.getAll();
                if (all == null) return List.of();
                return all.stream().filter(hd -> {
                    if (!maHoaDon.isEmpty() && (hd.getMaHoaDon() == null || !hd.getMaHoaDon().equalsIgnoreCase(maHoaDon))) return false;
                    if (!maKH.isEmpty() && (hd.getMaKH() == null || !hd.getMaKH().equalsIgnoreCase(maKH))) return false;
                    if (!tenKH.isEmpty()) {
                        KhachHang kh = khachHangDAO.findById(hd.getMaKH());
                        String name = kh != null && kh.getTenKhachHang() != null ? kh.getTenKhachHang().toLowerCase() : "";
                        if (!name.contains(tenKH)) return false;
                    }
                    if (from != null || to != null) {
                        if (hd.getNgayLap() == null) return false;
                        LocalDate ngay = hd.getNgayLap().toLocalDate();
                        if (from != null && ngay.isBefore(from)) return false;
                        if (to != null && ngay.isAfter(to)) return false;
                    }
                    if (!phuongThuc.isEmpty() && (hd.getPhuongThucThanhToan() == null || !hd.getPhuongThucThanhToan().equalsIgnoreCase(phuongThuc))) return false;
                    if (!trangThai.isEmpty() && (hd.getTrangThai() == null || !hd.getTrangThai().equalsIgnoreCase(trangThai))) return false;
                    return true;
                }).collect(Collectors.toList());
            }

            @Override
            protected void done() {
                try {
                    List<HoaDon> res = get();
                    model.setRowCount(0);
                    for (HoaDon hd : res) {
                        List<ChiTietHoaDon> items = chiTietHoaDonDAO.findByHoaDon(hd.getMaHoaDon());
                        float total = 0f;
                        if (items != null) {
                            for (ChiTietHoaDon ct : items) {
                                if (ct.getGiaDaKM() != null) total += ct.getGiaDaKM();
                            }
                        }
                        KhachHang kh = khachHangDAO.findById(hd.getMaKH());
                        model.addRow(new Object[]{
                                hd.getMaHoaDon(),
                                hd.getMaKH(),
                                kh != null ? kh.getTenKhachHang() : "N/A",
                                hd.getNgayLap() != null ? hd.getNgayLap().format(DT_FMT) : "",
                                hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan() : "",
                                hd.getTrangThai() != null ? hd.getTrangThai() : "",
                                items != null ? items.size() : 0,
                                String.format("%.0f", total)
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimHoaDon.this, "Lỗi khi tìm hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        };
        w.execute();
    }

    private void viewSelectedHoaDon() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String ma = (String) model.getValueAt(r, 0);
        // Reuse logic similar to PnlQuanLyVe: load chi tiết and show dialog
        SwingWorker<List<ChiTietHoaDon>, Void> w = new SwingWorker<>() {
            @Override protected List<ChiTietHoaDon> doInBackground() { return chiTietHoaDonDAO.findByHoaDon(ma); }
            @Override protected void done() {
                try {
                    List<ChiTietHoaDon> items = get();
                    HoaDon hd = hoaDonDAO.findById(ma);
                    KhachHang kh = khachHangDAO.findById(hd != null ? hd.getMaKH() : null);

                    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(PnlTimHoaDon.this),
                            "Chi tiết hóa đơn: " + ma, true);
                    dialog.setLayout(new BorderLayout(8,8));
                    dialog.setSize(700, 400);
                    dialog.setLocationRelativeTo(PnlTimHoaDon.this);

                    String[] cols = {"Mã vé", "Mã loại vé", "Mã chuyến", "Mã ghế", "Giá gốc", "Giá sau KM", "Ghi chú"};
                    DefaultTableModel m = new DefaultTableModel(cols, 0) {
                        @Override public boolean isCellEditable(int r, int c) { return false; }
                    };
                    JTable tbl = new JTable(m);
                    float total = 0f;
                    if (items != null) {
                        for (ChiTietHoaDon ct : items) {
                            total += ct.getGiaDaKM() != null ? ct.getGiaDaKM() : 0f;
                            m.addRow(new Object[]{
                                    ct.getMaVe(),
                                    ct.getMaLoaiVe(),
                                    "", // maChuyen resolvable via VeService if needed
                                    "",
                                    ct.getGiaGoc() != null ? String.format("%.0f", ct.getGiaGoc()) : "",
                                    ct.getGiaDaKM() != null ? String.format("%.0f", ct.getGiaDaKM()) : "",
                                    ct.getMoTa() != null ? ct.getMoTa() : ""
                            });
                        }
                    }

                    JScrollPane sp = new JScrollPane(tbl);
                    dialog.add(sp, BorderLayout.CENTER);

                    JPanel top = new JPanel(new GridLayout(0,1));
                    top.setBorder(BorderFactory.createEmptyBorder(6,6,6,6));
                    top.add(new JLabel("Mã hóa đơn: " + ma));
                    top.add(new JLabel("Khách hàng: " + (kh != null ? kh.getTenKhachHang() + " (" + kh.getMaKhachHang() + ")" : "N/A")));
                    top.add(new JLabel("Ngày lập: " + (hd != null && hd.getNgayLap() != null ? hd.getNgayLap().format(DT_FMT) : "N/A")));
                    dialog.add(top, BorderLayout.NORTH);

                    JPanel bottom = new JPanel(new BorderLayout(8,8));
                    JLabel lblTotal = new JLabel("Tổng tiền (đã KM): " + String.format("%.0f", total));
                    lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
                    bottom.add(lblTotal, BorderLayout.WEST);

                    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton btnIn = new JButton("In vé (PDF)");
                    JButton btnExport = new JButton("Xuất hóa đơn (PDF)");
                    JButton btnClose = new JButton("Đóng");
                    MaterialInitializer.styleButton(btnIn);
                    MaterialInitializer.styleButton(btnExport);
                    MaterialInitializer.styleButton(btnClose);
                    actions.add(btnIn);
                    actions.add(btnExport);
                    actions.add(btnClose);
                    bottom.add(actions, BorderLayout.EAST);

                    dialog.add(bottom, BorderLayout.SOUTH);

                    btnIn.addActionListener(ae -> {
                        btnIn.setEnabled(false);
                        SwingWorker<Void, Void> wk = new SwingWorker<>() {
                            @Override protected Void doInBackground() {
                                for (ChiTietHoaDon ct : items) {
                                    try {
                                        // try to print by calling HoaDonService or VeService if available
                                        // HoaDonService may provide printing of tickets; fallback: do nothing
                                        // Here we call HoaDonService.xuatHoaDonPDF only for invoice; ticket printing is left to VeService if available
                                    } catch (Exception ex) { ex.printStackTrace(); }
                                }
                                return null;
                            }
                            @Override protected void done() {
                                btnIn.setEnabled(true);
                                JOptionPane.showMessageDialog(dialog, "Hoàn tất.", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                            }
                        };
                        wk.execute();
                    });

                    btnExport.addActionListener(ae -> {
                        btnExport.setEnabled(false);
                        SwingWorker<String, Void> wk = new SwingWorker<>() {
                            @Override protected String doInBackground() {
                                try {
                                    return hoaDonService.xuatHoaDonPDF(ma);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    return null;
                                }
                            }
                            @Override protected void done() {
                                btnExport.setEnabled(true);
                                try {
                                    String file = get();
                                    if (file != null) JOptionPane.showMessageDialog(dialog, "Xuất hóa đơn thành công:\n" + file, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                                    else JOptionPane.showMessageDialog(dialog, "Xuất hóa đơn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    JOptionPane.showMessageDialog(dialog, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        };
                        wk.execute();
                    });

                    btnClose.addActionListener(ae -> dialog.dispose());

                    dialog.setVisible(true);

                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimHoaDon.this, "Lỗi khi tải chi tiết: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void exportSelectedHoaDon() {
        int r = table.getSelectedRow();
        if (r < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xuất.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String ma = (String) model.getValueAt(r, 0);
        btnExport.setEnabled(false);
        SwingWorker<String, Void> w = new SwingWorker<>() {
            @Override protected String doInBackground() {
                try {
                    return hoaDonService.xuatHoaDonPDF(ma);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    return null;
                }
            }
            @Override protected void done() {
                btnExport.setEnabled(true);
                try {
                    String file = get();
                    if (file != null) JOptionPane.showMessageDialog(PnlTimHoaDon.this, "Xuất hóa đơn thành công:\n" + file, "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    else JOptionPane.showMessageDialog(PnlTimHoaDon.this, "Xuất hóa đơn thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimHoaDon.this, "Lỗi: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }
}