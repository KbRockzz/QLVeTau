package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.KhachHang;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * PnlTimKhachHang - panel tìm kiếm / quản lý nhanh Khách hàng (SwingWorker version).
 *
 * Yêu cầu:
 *  - Không có cột "Trạng thái".
 *  - Không có chức năng phục hồi.
 *  - Sử dụng SwingWorker cho các tác vụ IO/DB.
 */
public class PnlTimKhachHang extends JPanel {
    private final KhachHangDAO khDao = KhachHangDAO.getInstance();

    
    private final JTextField txtFilterMa = new JTextField(20);
    private final JTextField txtFilterTen = new JTextField(20);
    private final JTextField txtFilterSdt = new JTextField(20);

    
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Mã KH", "Tên", "Email", "SĐT"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable table = new JTable(model);

    
    private final JButton btnSearch = new JButton("Tìm");
    private final JButton btnReset = new JButton("Đặt lại");
    private final JButton btnView = new JButton("Xem");
    private final JButton btnDelete = new JButton("Xóa");

    public PnlTimKhachHang() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        initFilterPanel();
        initTablePanel();
        initActionPanel();
        doSearch();

        try { UIUtils.adjustTableForScale(table, 1.1f); } catch (Throwable ignored) {}
    }

    private void initFilterPanel() {
        JPanel pnl = new JPanel(new GridBagLayout());
        pnl.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm"));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;

        int row = 0;


        c.gridx = 0; c.gridy = row; c.weightx = 1.0;
        pnl.add(Box.createHorizontalStrut(0), c);


        c.gridx = 1; c.weightx = 0;
        pnl.add(new JLabel("Mã khách hàng:"), c);


        c.gridx = 2;
        pnl.add(txtFilterMa, c);


        c.gridx = 3; c.weightx = 1.0;
        pnl.add(Box.createHorizontalStrut(0), c);

        row++;


        c.gridx = 0; c.gridy = row; c.weightx = 1.0;
        pnl.add(Box.createHorizontalStrut(0), c);

        c.gridx = 1; c.weightx = 0;
        pnl.add(new JLabel("Tên khách hàng:"), c);

        c.gridx = 2;
        pnl.add(txtFilterTen, c);

        c.gridx = 3; c.weightx = 1.0;
        pnl.add(Box.createHorizontalStrut(0), c);

        row++;


        c.gridx = 0; c.gridy = row; c.weightx = 1.0;
        pnl.add(Box.createHorizontalStrut(0), c);

        c.gridx = 1; c.weightx = 0;
        pnl.add(new JLabel("Số điện thoại:"), c);

        c.gridx = 2;
        pnl.add(txtFilterSdt, c);

        c.gridx = 3; c.weightx = 1.0;
        pnl.add(Box.createHorizontalStrut(0), c);


        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        MaterialInitializer.styleButton(btnSearch);
        MaterialInitializer.styleButton(btnReset);
        btns.add(btnSearch);
        btns.add(btnReset);

        c.gridx = 0; c.gridy = ++row; c.gridwidth = 4; pnl.add(btns, c);
        c.gridwidth = 1;

        add(pnl, BorderLayout.NORTH);

        btnSearch.addActionListener(e -> doSearch());
        btnReset.addActionListener(e -> resetFilters());
    }

    private void initTablePanel() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Kết quả"));
        add(sp, BorderLayout.CENTER);

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) viewSelected();
            }
        });
    }

    private void initActionPanel() {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        MaterialInitializer.styleButton(btnView);
        MaterialInitializer.styleButton(btnDelete);
        pnl.add(btnView);
        pnl.add(btnDelete);
        add(pnl, BorderLayout.SOUTH);

        btnView.addActionListener(e -> viewSelected());
        btnDelete.addActionListener(e -> deleteSelected());
    }

    private void doSearch() {
        final String ma = txtFilterMa.getText().trim();
        final String ten = txtFilterTen.getText().trim().toLowerCase();
        final String sdt = txtFilterSdt.getText().trim();

        btnSearch.setEnabled(false);
        model.setRowCount(0);

        SwingWorker<List<KhachHang>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<KhachHang> doInBackground() {
                
                //List<KhachHang> all = khDao.getAllIncludingDeleted();
                List<KhachHang> all = khDao.getAll();
                if (all == null) return List.of();
                return all.stream().filter(kh -> {
                    if (!ma.isEmpty() && (kh.getMaKhachHang() == null || !kh.getMaKhachHang().equalsIgnoreCase(ma))) return false;
                    if (!ten.isEmpty() && (kh.getTenKhachHang() == null || !kh.getTenKhachHang().toLowerCase().contains(ten))) return false;
                    if (!sdt.isEmpty() && (kh.getSoDienThoai() == null || !kh.getSoDienThoai().contains(sdt))) return false;
                    return true;
                }).toList();
            }

            @Override
            protected void done() {
                try {
                    List<KhachHang> res = get();
                    model.setRowCount(0);
                    for (KhachHang kh : res) {
                        model.addRow(new Object[]{
                                kh.getMaKhachHang(),
                                kh.getTenKhachHang(),
                                kh.getEmail(),
                                kh.getSoDienThoai()
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKhachHang.this, "Lỗi khi tìm kiếm: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnSearch.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private void resetFilters() {
        txtFilterMa.setText("");
        txtFilterTen.setText("");
        txtFilterSdt.setText("");
        model.setRowCount(0);
    }

    private String getSelectedMaKH() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        Object o = model.getValueAt(r, 0);
        return o != null ? o.toString() : null;
    }

    private void viewSelected() {
        String ma = getSelectedMaKH();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        btnView.setEnabled(false);
        SwingWorker<KhachHang, Void> worker = new SwingWorker<>() {
            @Override
            protected KhachHang doInBackground() {
                return khDao.findById(ma);
            }

            @Override
            protected void done() {
                btnView.setEnabled(true);
                try {
                    KhachHang kh = get();
                    if (kh == null) {
                        JOptionPane.showMessageDialog(PnlTimKhachHang.this, "Không tìm thấy khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Mã KH: ").append(kh.getMaKhachHang()).append("\n");
                    sb.append("Tên: ").append(kh.getTenKhachHang()).append("\n");
                    sb.append("Email: ").append(kh.getEmail()).append("\n");
                    sb.append("SĐT: ").append(kh.getSoDienThoai()).append("\n");
                    JOptionPane.showMessageDialog(PnlTimKhachHang.this, sb.toString(), "Chi tiết khách hàng", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKhachHang.this, "Lỗi khi tải thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void deleteSelected() {
        String ma = getSelectedMaKH();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Xác nhận xóa (soft delete) khách hàng " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        btnDelete.setEnabled(false);
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return khDao.delete(ma);
            }

            @Override
            protected void done() {
                btnDelete.setEnabled(true);
                try {
                    Boolean ok = get();
                    if (ok != null && ok) {
                        JOptionPane.showMessageDialog(PnlTimKhachHang.this, "Đã hủy khách hàng (soft).", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        doSearch();
                    } else {
                        JOptionPane.showMessageDialog(PnlTimKhachHang.this, "Không thể hủy khách hàng.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKhachHang.this, "Lỗi khi xử lý.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}