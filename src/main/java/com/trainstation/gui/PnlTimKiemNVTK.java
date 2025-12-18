package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.LoaiNVDAO;
import com.trainstation.dao.NhanVienDAO;
import com.trainstation.dao.TaiKhoanDAO;
import com.trainstation.model.LoaiNV;
import com.trainstation.model.NhanVien;
import com.trainstation.model.TaiKhoan;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PnlTimKiemNVTK extends JPanel {
    private final NhanVienDAO nhanVienDAO = NhanVienDAO.getInstance();
    private final TaiKhoanDAO taiKhoanDAO = TaiKhoanDAO.getInstance();
    private final LoaiNVDAO loaiNVDAO = LoaiNVDAO.getInstance();

    private final JTabbedPane tabs = new JTabbedPane();

    // --- Components for Nhân Viên tab ---
    private final JTextField txtNvMa = new JTextField(20);
    private final JTextField txtNvTen = new JTextField(20);
    private final JTextField txtNvSdt = new JTextField(20);
    private final JComboBox<String> cmbNvMaLoai = new JComboBox<>();

    private final DefaultTableModel modelNv = new DefaultTableModel(
            new String[]{"Mã NV", "Tên", "SĐT", "Địa chỉ", "Ngày sinh", "Mã loại"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tblNv = new JTable(modelNv);

    private final JButton btnNvSearch = new JButton("Tìm");
    private final JButton btnNvReset = new JButton("Đặt lại");
    private final JButton btnNvView = new JButton("Xem");
    private final JButton btnNvDelete = new JButton("Xóa");

    // --- Components for Tài Khoản tab ---
    private final JTextField txtTkMa = new JTextField(20);
    private final JTextField txtTkMaNv = new JTextField(20);
    private final JTextField txtTkTen = new JTextField(20);
    private final JCheckBox chkTkActiveOnly = new JCheckBox("Chỉ hiện tài khoản hoạt động", true);

    private final DefaultTableModel modelTk = new DefaultTableModel(
            new String[]{"Mã TK", "Mã NV", "Tài khoản", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int col) { return false; }
    };
    private final JTable tblTk = new JTable(modelTk);

    private final JButton btnTkSearch = new JButton("Tìm");
    private final JButton btnTkReset = new JButton("Đặt lại");
    private final JButton btnTkView = new JButton("Xem");
    private final JButton btnTkDelete = new JButton("Xóa");

    public PnlTimKiemNVTK() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        initNhanVienTab();
        searchNhanVien();
        initTaiKhoanTab();
        searchTaiKhoan();

        add(tabs, BorderLayout.CENTER);

        try { UIUtils.adjustTableForScale(tblNv, 1.1f); UIUtils.adjustTableForScale(tblTk, 1.1f); } catch (Throwable ignored) {}
    }

    private void initNhanVienTab() {
        JPanel pnl = new JPanel(new BorderLayout(8,8));

        // populate LoaiNV combo
        cmbNvMaLoai.removeAllItems();
        cmbNvMaLoai.addItem(""); // empty = any
        try {
            List<LoaiNV> loaiList = loaiNVDAO.getAll();
            if (loaiList != null) {
                for (LoaiNV l : loaiList) {
                    if (l != null && l.getMaLoai() != null) cmbNvMaLoai.addItem(l.getMaLoai());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Filter panel
        JPanel filter = new JPanel(new GridBagLayout());
        filter.setBorder(BorderFactory.createTitledBorder("Bộ lọc - Nhân viên"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx = 0; c.gridy = row; filter.add(new JLabel("Mã NV:"), c);
        c.gridx = 1; filter.add(txtNvMa, c);
        c.gridx = 2; filter.add(new JLabel("Tên:"), c);
        c.gridx = 3; filter.add(txtNvTen, c);
        row++;

        c.gridx = 0; c.gridy = row; filter.add(new JLabel("SĐT:"), c);
        c.gridx = 1; filter.add(txtNvSdt, c);
        c.gridx = 2; filter.add(new JLabel("Mã loại NV:"), c);
        c.gridx = 3; filter.add(cmbNvMaLoai, c);
        row++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        MaterialInitializer.styleButton(btnNvSearch);
        MaterialInitializer.styleButton(btnNvReset);
        MaterialInitializer.styleButton(btnNvView);
        MaterialInitializer.styleButton(btnNvDelete);
        btns.add(btnNvSearch);
        btns.add(btnNvReset);
        btns.add(btnNvView);
        btns.add(btnNvDelete);

        c.gridx = 0; c.gridy = row; c.gridwidth = 4; filter.add(btns, c);
        c.gridwidth = 1;

        pnl.add(filter, BorderLayout.NORTH);

        // Table
        tblNv.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(tblNv);
        sp.setBorder(BorderFactory.createTitledBorder("Kết quả - Nhân viên"));
        pnl.add(sp, BorderLayout.CENTER);

        tabs.addTab("Nhân Viên", pnl);

        // Actions
        btnNvSearch.addActionListener(e -> searchNhanVien());
        btnNvReset.addActionListener(e -> {
            txtNvMa.setText(""); txtNvTen.setText(""); txtNvSdt.setText(""); cmbNvMaLoai.setSelectedIndex(0);
            modelNv.setRowCount(0);
        });
        btnNvView.addActionListener(e -> viewSelectedNhanVien());
        btnNvDelete.addActionListener(e -> deleteSelectedNhanVien());

        tblNv.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                // enable/disable view/delete based on selection
                boolean sel = tblNv.getSelectedRow() >= 0;
                btnNvView.setEnabled(sel);
                btnNvDelete.setEnabled(sel);
            }
        });

        tblNv.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) viewSelectedNhanVien();
            }
        });
    }

    private void initTaiKhoanTab() {
        JPanel pnl = new JPanel(new BorderLayout(8,8));

        // Filter panel
        JPanel filter = new JPanel(new GridBagLayout());
        filter.setBorder(BorderFactory.createTitledBorder("Bộ lọc - Tài khoản"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        c.gridx = 0; c.gridy = row; filter.add(new JLabel("Mã TK:"), c);
        c.gridx = 1; filter.add(txtTkMa, c);
        c.gridx = 2; filter.add(new JLabel("Mã NV:"), c);
        c.gridx = 3; filter.add(txtTkMaNv, c);
        row++;

        c.gridx = 0; c.gridy = row; filter.add(new JLabel("Tài khoản:"), c);
        c.gridx = 1; filter.add(txtTkTen, c);
        c.gridx = 2; filter.add(chkTkActiveOnly, c);
        row++;

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        MaterialInitializer.styleButton(btnTkSearch);
        MaterialInitializer.styleButton(btnTkReset);
        MaterialInitializer.styleButton(btnTkView);
        MaterialInitializer.styleButton(btnTkDelete);
        btns.add(btnTkSearch);
        btns.add(btnTkReset);
        btns.add(btnTkView);
        btns.add(btnTkDelete);

        c.gridx = 0; c.gridy = ++row; c.gridwidth = 4; filter.add(btns, c);
        c.gridwidth = 1;

        pnl.add(filter, BorderLayout.NORTH);

        // Table
        tblTk.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(tblTk);
        sp.setBorder(BorderFactory.createTitledBorder("Kết quả - Tài khoản"));
        pnl.add(sp, BorderLayout.CENTER);

        tabs.addTab("Tài Khoản", pnl);

        // Actions
        btnTkSearch.addActionListener(e -> searchTaiKhoan());
        btnTkReset.addActionListener(e -> {
            txtTkMa.setText(""); txtTkMaNv.setText(""); txtTkTen.setText(""); chkTkActiveOnly.setSelected(true);
            modelTk.setRowCount(0);
        });
        btnTkView.addActionListener(e -> viewSelectedTaiKhoan());
        btnTkDelete.addActionListener(e -> deleteSelectedTaiKhoan());

        tblTk.getSelectionModel().addListSelectionListener((ListSelectionEvent e) -> {
            if (!e.getValueIsAdjusting()) {
                boolean sel = tblTk.getSelectedRow() >= 0;
                btnTkView.setEnabled(sel);
                btnTkDelete.setEnabled(sel);
            }
        });

        tblTk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) viewSelectedTaiKhoan();
            }
        });
    }

    // --------------------- Nhân viên actions ---------------------

    private void searchNhanVien() {
        final String ma = txtNvMa.getText().trim();
        final String ten = txtNvTen.getText().trim().toLowerCase();
        final String sdt = txtNvSdt.getText().trim();
        final String maLoai = (String) cmbNvMaLoai.getSelectedItem();

        btnNvSearch.setEnabled(false);
        modelNv.setRowCount(0);

        SwingWorker<List<NhanVien>, Void> w = new SwingWorker<>() {
            @Override protected List<NhanVien> doInBackground() {
                // search active employees only
                List<NhanVien> all = nhanVienDAO.getAll();
                if (all == null) return List.of();
                return all.stream().filter(nv -> {
                    if (!ma.isEmpty() && (nv.getMaNV() == null || !nv.getMaNV().equalsIgnoreCase(ma))) return false;
                    if (!ten.isEmpty() && (nv.getTenNV() == null || !nv.getTenNV().toLowerCase().contains(ten))) return false;
                    if (!sdt.isEmpty() && (nv.getSoDienThoai() == null || !nv.getSoDienThoai().contains(sdt))) return false;
                    if (maLoai != null && !maLoai.trim().isEmpty() && (nv.getMaLoaiNV() == null || !nv.getMaLoaiNV().equalsIgnoreCase(maLoai))) return false;
                    return true;
                }).toList();
            }

            @Override protected void done() {
                try {
                    List<NhanVien> res = get();
                    modelNv.setRowCount(0);
                    for (NhanVien nv : res) {
                        modelNv.addRow(new Object[]{
                                nv.getMaNV(),
                                nv.getTenNV(),
                                nv.getSoDienThoai(),
                                nv.getDiaChi(),
                                nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "",
                                nv.getMaLoaiNV()
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Lỗi khi tìm nhân viên: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnNvSearch.setEnabled(true);
                }
            }
        };
        w.execute();
    }

    private void viewSelectedNhanVien() {
        int r = tblNv.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        String ma = (String) modelNv.getValueAt(r, 0);

        btnNvView.setEnabled(false);
        SwingWorker<NhanVien, Void> w = new SwingWorker<>() {
            @Override protected NhanVien doInBackground() { return nhanVienDAO.findById(ma); }
            @Override protected void done() {
                btnNvView.setEnabled(true);
                try {
                    NhanVien nv = get();
                    if (nv == null) { JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Không tìm thấy.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Mã NV: ").append(nv.getMaNV()).append("\n");
                    sb.append("Tên: ").append(nv.getTenNV()).append("\n");
                    sb.append("SĐT: ").append(nv.getSoDienThoai()).append("\n");
                    sb.append("Địa chỉ: ").append(nv.getDiaChi()).append("\n");
                    sb.append("Ngày sinh: ").append(nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "").append("\n");
                    sb.append("Mã loại: ").append(nv.getMaLoaiNV()).append("\n");
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, sb.toString(), "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Lỗi khi tải thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void deleteSelectedNhanVien() {
        int r = tblNv.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        String ma = (String) modelNv.getValueAt(r, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Xác nhận hủy (soft delete) nhân viên " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        btnNvDelete.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() { return nhanVienDAO.delete(ma); }
            @Override protected void done() {
                btnNvDelete.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Đã hủy nhân viên (soft).", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        searchNhanVien();
                    } else {
                        JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Không thể hủy nhân viên.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Lỗi khi xử lý.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    // --------------------- Tài khoản actions ---------------------

    private void searchTaiKhoan() {
        final String maTk = txtTkMa.getText().trim();
        final String maNv = txtTkMaNv.getText().trim();
        final String ten = txtTkTen.getText().trim().toLowerCase();
        final boolean onlyActive = chkTkActiveOnly.isSelected();

        btnTkSearch.setEnabled(false);
        modelTk.setRowCount(0);

        SwingWorker<List<TaiKhoan>, Void> w = new SwingWorker<>() {
            @Override protected List<TaiKhoan> doInBackground() {
                // getAll() already filters by isActive=1 in database
                List<TaiKhoan> all = taiKhoanDAO.getAll();
                if (all == null) return List.of();
                return all.stream().filter(tk -> {
                    if (!maTk.isEmpty() && (tk.getMaTK() == null || !tk.getMaTK().equalsIgnoreCase(maTk))) return false;
                    if (!maNv.isEmpty() && (tk.getMaNV() == null || !tk.getMaNV().equalsIgnoreCase(maNv))) return false;
                    if (!ten.isEmpty() && (tk.getTenTaiKhoan() == null || !tk.getTenTaiKhoan().toLowerCase().contains(ten))) return false;
                    // onlyActive filter is handled by getAll() which queries WHERE isActive=1
                    return true;
                }).toList();
            }

            @Override protected void done() {
                try {
                    List<TaiKhoan> res = get();
                    modelTk.setRowCount(0);
                    for (TaiKhoan tk : res) {
                        modelTk.addRow(new Object[]{
                                tk.getMaTK(),
                                tk.getMaNV(),
                                tk.getTenTaiKhoan(),
                                tk.getTrangThai()
                        });
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Lỗi khi tìm tài khoản: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnTkSearch.setEnabled(true);
                }
            }
        };
        w.execute();
    }

    private void viewSelectedTaiKhoan() {
        int r = tblTk.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        String ma = (String) modelTk.getValueAt(r, 0);

        btnTkView.setEnabled(false);
        SwingWorker<TaiKhoan, Void> w = new SwingWorker<>() {
            @Override protected TaiKhoan doInBackground() { return taiKhoanDAO.findById(ma); }
            @Override protected void done() {
                btnTkView.setEnabled(true);
                try {
                    TaiKhoan tk = get();
                    if (tk == null) { JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Không tìm thấy.", "Lỗi", JOptionPane.ERROR_MESSAGE); return; }
                    StringBuilder sb = new StringBuilder();
                    sb.append("Mã TK: ").append(tk.getMaTK()).append("\n");
                    sb.append("Mã NV: ").append(tk.getMaNV()).append("\n");
                    sb.append("Tài khoản: ").append(tk.getTenTaiKhoan()).append("\n");
                    sb.append("Trạng thái: ").append(tk.getTrangThai()).append("\n");
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, sb.toString(), "Chi tiết tài khoản", JOptionPane.INFORMATION_MESSAGE);
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Lỗi khi tải thông tin.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }

    private void deleteSelectedTaiKhoan() {
        int r = tblTk.getSelectedRow();
        if (r < 0) { JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản để hủy.", "Thông báo", JOptionPane.INFORMATION_MESSAGE); return; }
        String ma = (String) modelTk.getValueAt(r, 0);
        int conf = JOptionPane.showConfirmDialog(this, "Xác nhận hủy (soft delete) tài khoản " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        btnTkDelete.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() { return taiKhoanDAO.delete(ma); }
            @Override protected void done() {
                btnTkDelete.setEnabled(true);
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Đã hủy tài khoản (soft).", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        searchTaiKhoan();
                    } else {
                        JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Không thể hủy tài khoản.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlTimKiemNVTK.this, "Lỗi khi xử lý.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        w.execute();
    }
}