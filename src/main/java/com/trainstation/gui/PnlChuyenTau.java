package com.trainstation.gui;

import com.trainstation.dao.ChuyenTauDAO;
import com.trainstation.dao.VeDAO;
import com.trainstation.model.ChuyenTau;
import com.trainstation.util.UIUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Panel quản lý chuyến tàu (CRUD inline, không dùng dialog).
 * - Dùng trực tiếp ChuyenTauDAO để insert/update/delete, start và arrive.
 * - Dùng ChuyenTauDAO.countTicketsForChuyenOnDate(...) để đếm vé.
 * - Có form nhỏ ở trên để thêm/sửa; khi chọn hàng, form được populate.
 */
public class PnlChuyenTau extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;

    private final JTextField txtMaChuyen;
    private final JTextField txtMaTau;
    private final JTextField txtGaDi;
    private final JTextField txtGaDen;
    private final JSpinner spinnerDateTime;
    private final JCheckBox chkTemplate;

    private final JButton btnRefresh, btnAdd, btnUpdate, btnDelete, btnStart, btnArrived;

    private final JSpinner dateSpinner; // chọn ngày để hiển thị danh sách
    private final ChuyenTauDAO chuyenTauDAO;
    private final VeDAO veDAO;

    private ScheduledExecutorService scheduler;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PnlChuyenTau() {
        chuyenTauDAO = ChuyenTauDAO.getInstance();
        veDAO = VeDAO.getInstance();

        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Top: date selector + buttons
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.add(new JLabel("Chọn ngày:"));
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        topBar.add(dateSpinner);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        topBar.add(btnRefresh);

        add(topBar, BorderLayout.NORTH);

        // Middle: left = table, right = inline form
        model = new DefaultTableModel(new String[]{"Mã chuyến", "Mã tàu", "Ga đi", "Ga đến", "Ngày giờ chạy", "Số vé (ngày)", "Trạng thái"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến"));

        // Form panel
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến (inline)"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;
        int row = 0;

        txtMaChuyen = new JTextField(15);
        txtMaTau = new JTextField(15);
        txtGaDi = new JTextField(15);
        txtGaDen = new JTextField(15);

        SpinnerDateModel dtModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE);
        spinnerDateTime = new JSpinner(dtModel);
        spinnerDateTime.setEditor(new JSpinner.DateEditor(spinnerDateTime, "dd/MM/yyyy HH:mm"));

        chkTemplate = new JCheckBox("Template (không có ngày giờ chạy)");
        chkTemplate.addActionListener(e -> spinnerDateTime.setEnabled(!chkTemplate.isSelected()));

        // Helper to add rows
        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Mã chuyến:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(txtMaChuyen, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Mã tàu:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(txtMaTau, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ga đi:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(txtGaDi, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ga đến:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(txtGaDen, c);

        c.gridx = 0; c.gridy = row; c.weightx = 0.0; form.add(new JLabel("Ngày giờ chạy:"), c);
        c.gridx = 1; c.gridy = row++; c.weightx = 1.0; form.add(spinnerDateTime, c);

        c.gridx = 0; c.gridy = row; c.gridwidth = 2; form.add(chkTemplate, c);
        c.gridwidth = 1;

        // Buttons under form
        JPanel formBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAdd = new JButton("Thêm");
        btnUpdate = new JButton("Cập nhật");
        btnDelete = new JButton("Xóa");
        btnStart = new JButton("Khởi hành");
        btnArrived = new JButton("Đến nơi");

        formBtns.add(btnAdd);
        formBtns.add(btnUpdate);
        formBtns.add(btnDelete);
        formBtns.add(btnStart);
        formBtns.add(btnArrived);

        c.gridx = 0; c.gridy = ++row; c.gridwidth = 2; form.add(formBtns, c);

        // Split pane
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sp, form);
        split.setResizeWeight(0.65);
        add(split, BorderLayout.CENTER);

        // Bottom info
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(new JLabel("Chọn 1 chuyến để sửa hoặc thao tác. Thêm/chỉnh sửa sẽ cập nhật DB trực tiếp."), BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);

        // Listeners
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) populateFormFromSelection();
            }
        });

        btnAdd.addActionListener(e -> addChuyen());
        btnUpdate.addActionListener(e -> updateChuyen());
        btnDelete.addActionListener(e -> deleteChuyen());
        btnStart.addActionListener(e -> startSelected());
        btnArrived.addActionListener(e -> arrivedSelected());

        try { UIUtils.adjustTableForScale(table, 1.2f); } catch (Throwable ignored) {}

        startAutoStarter();
        loadData();
    }

    private LocalDate getSelectedDate() {
        Date d = (Date) dateSpinner.getValue();
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void loadData() {
        btnRefresh.setEnabled(false);
        model.setRowCount(0);
        LocalDate date = getSelectedDate();

        SwingWorker<Void, Object[]> w = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<ChuyenTau> list = chuyenTauDAO.getAll();
                if (list == null) return null;
                for (ChuyenTau c : list) {
                    try {
                        boolean include = false;
                        LocalDateTime runTime = c.getGioDi();
                        if (runTime != null) {
                            include = runTime.toLocalDate().equals(date);
                        } else {
                            // template: include if there are tickets for that date
                            int cnt = chuyenTauDAO.countTicketsForChuyenOnDate(c.getMaChuyen(), date);
                            include = cnt > 0;
                        }
                        if (!include) continue;
                        int count = chuyenTauDAO.countTicketsForChuyenOnDate(c.getMaChuyen(), date);
                        String runTimeStr = runTime != null ? runTime.format(DT_FMT) : "";
                        String status = c.getTrangThai() != null ? c.getTrangThai() : "";
                        publish(new Object[]{
                                c.getMaChuyen(),
                                c.getMaTau(),
                                c.getGaDi(),
                                c.getGaDen(),
                                runTimeStr,
                                count,
                                status
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                for (Object[] r : chunks) model.addRow(r);
            }

            @Override
            protected void done() {
                btnRefresh.setEnabled(true);
                try { UIUtils.adjustTableForScale(table, 1.2f); } catch (Throwable ignored) {}
            }
        };
        w.execute();
    }

    private void populateFormFromSelection() {
        int r = table.getSelectedRow();
        if (r < 0) {
            clearForm();
            return;
        }
        String ma = (String) model.getValueAt(r, 0);
        if (ma == null) return;
        SwingWorker<ChuyenTau, Void> w = new SwingWorker<>() {
            @Override
            protected ChuyenTau doInBackground() {
                return chuyenTauDAO.findById(ma);
            }
            @Override
            protected void done() {
                try {
                    ChuyenTau ct = get();
                    if (ct == null) return;
                    txtMaChuyen.setText(ct.getMaChuyen());
                    txtMaChuyen.setEnabled(false); // khóa mã khi edit
                    txtMaTau.setText(ct.getMaTau());
                    txtGaDi.setText(ct.getGaDi());
                    txtGaDen.setText(ct.getGaDen());
                    if (ct.getGioDi() != null) {
                        Date d = java.util.Date.from(ct.getGioDi().atZone(ZoneId.systemDefault()).toInstant());
                        spinnerDateTime.setValue(d);
                        chkTemplate.setSelected(false);
                        spinnerDateTime.setEnabled(true);
                    } else {
                        chkTemplate.setSelected(true);
                        spinnerDateTime.setEnabled(false);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void clearForm() {
        txtMaChuyen.setText("");
        txtMaChuyen.setEnabled(true);
        txtMaTau.setText("");
        txtGaDi.setText("");
        txtGaDen.setText("");
        spinnerDateTime.setValue(new Date());
        chkTemplate.setSelected(false);
        spinnerDateTime.setEnabled(true);
    }

    private void addChuyen() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mã chuyến không được để trống.", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        ChuyenTau ct = buildChuyenFromForm();
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return chuyenTauDAO.insert(ct);
            }
            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã thêm chuyến.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể thêm chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void updateChuyen() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hoặc nhập mã chuyến.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ChuyenTau ct = buildChuyenFromForm();
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return chuyenTauDAO.update(ct);
            }
            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã cập nhật chuyến.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                    } else {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể cập nhật chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private void deleteChuyen() {
        String ma = txtMaChuyen.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để xóa.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int conf = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chuyến " + ma + "?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (conf != JOptionPane.YES_OPTION) return;

        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return chuyenTauDAO.delete(ma);
            }
            @Override
            protected void done() {
                try {
                    boolean ok = get();
                    if (ok) {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã xóa.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                        loadData();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(PnlChuyenTau.this, "Không thể xóa chuyến.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        w.execute();
    }

    private ChuyenTau buildChuyenFromForm() {
        ChuyenTau ct = new ChuyenTau();
        ct.setMaChuyen(txtMaChuyen.getText().trim());
        ct.setMaTau(txtMaTau.getText().trim());
        ct.setGaDi(txtGaDi.getText().trim());
        ct.setGaDen(txtGaDen.getText().trim());
        if (chkTemplate.isSelected()) {
            ct.setGioDi(null);
            ct.setGioDen(null);
        } else {
            Date d = (Date) spinnerDateTime.getValue();
            LocalDateTime ldt = Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
            ct.setGioDi(ldt);
            // giữ nguyên gioDen nếu không muốn set
            ct.setGioDen(null);
        }
        // không set maNV / soKm / maChang / trangThai ở form này — nếu cần, bạn có thể mở rộng form
        return ct;
    }

    private String getSelectedMaChuyenFromTable() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        return (String) model.getValueAt(r, 0);
    }

    private void startSelected() {
        String ma = getSelectedMaChuyenFromTable();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để khởi hành.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        LocalDate date = getSelectedDate();
        int cf = JOptionPane.showConfirmDialog(this, "Xác nhận đánh dấu chuyến " + ma + " ngày " + date + " là 'Đã khởi hành'?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (cf != JOptionPane.YES_OPTION) return;
        btnStart.setEnabled(false);
        SwingWorker<Boolean, Void> w = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                return chuyenTauDAO.startChuyenOnDate(ma, date, "UI");
            }
            @Override protected void done() {
                btnStart.setEnabled(true);
                try {
                    boolean ok = get();
                    JOptionPane.showMessageDialog(PnlChuyenTau.this, ok ? "Đã cập nhật: Khởi hành." : "Không thể cập nhật trạng thái.", "Kết quả", JOptionPane.INFORMATION_MESSAGE);
                    loadData();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void arrivedSelected() {
        String ma = getSelectedMaChuyenFromTable();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để đánh dấu đến nơi.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        LocalDate date = getSelectedDate();
        String[] options = {"Chỉ giải phóng vé chưa thanh toán", "Giải phóng cả vé đã thanh toán", "Hủy"};
        int choice = JOptionPane.showOptionDialog(this,
                "Chọn chính sách giải phóng ghế cho chuyến " + ma + " ngày " + date + ":",
                "Chính sách giải phóng ghế",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);
        if (choice == 2 || choice == JOptionPane.CLOSED_OPTION) return;
        boolean freePaid = (choice == 1);

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận đánh dấu chuyến " + ma + " ngày " + date + " là 'Đã đến' và giải phóng ghế?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        btnArrived.setEnabled(false);
        SwingWorker<Integer, Void> w = new SwingWorker<>() {
            @Override protected Integer doInBackground() {
                return chuyenTauDAO.arriveChuyenOnDate(ma, date, freePaid, "UI");
            }
            @Override protected void done() {
                btnArrived.setEnabled(true);
                try {
                    int updated = get();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PnlChuyenTau.this,
                            updated > 0 ? "Đã xử lý - ghế đã được giải phóng: " + updated : "Đã cập nhật trạng thái chuyến, nhưng không có ghế nào thay đổi.",
                            "Kết quả", JOptionPane.INFORMATION_MESSAGE));
                    loadData();
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        w.execute();
    }

    private void startAutoStarter() {
        scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "ChuyenTau-AutoStarter");
            t.setDaemon(true);
            return t;
        });
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkAndAutoStart();
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, 15, 60, TimeUnit.SECONDS);
    }

    private void checkAndAutoStart() {
        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        List<ChuyenTau> list = chuyenTauDAO.getAll();
        if (list == null) return;
        for (ChuyenTau c : list) {
            try {
                LocalDateTime gioDi = c.getGioDi();
                if (gioDi == null) continue; // template không tự start
                if (!gioDi.toLocalDate().equals(today)) continue;
                String status = c.getTrangThai();
                boolean alreadyStarted = status != null && status.toLowerCase().contains("khởi hành");
                if (alreadyStarted) continue;
                if (!gioDi.isAfter(now)) {
                    chuyenTauDAO.startChuyenOnDate(c.getMaChuyen(), today, "AUTO");
                    SwingUtilities.invokeLater(this::loadData);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Gọi khi panel/khung chứa panel đóng để shutdown scheduler.
     */
    public void dispose() {
        if (scheduler != null && !scheduler.isShutdown()) scheduler.shutdownNow();
    }
}