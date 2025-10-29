package com.trainstation.gui;

import com.trainstation.model.ChuyenTau;
import com.trainstation.model.Ve;
import com.trainstation.service.ChuyenTauService;
import com.trainstation.dao.VeDAO;
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

public class PnlChuyenTau extends JPanel {
    private final JTable table;
    private final DefaultTableModel model;
    private final JButton btnRefresh, btnStart, btnArrived;
    private final JSpinner dateSpinner;
    private final ChuyenTauService chuyenTauService;
    private final VeDAO veDAO;

    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PnlChuyenTau() {
        chuyenTauService = ChuyenTauService.getInstance();
        veDAO = VeDAO.getInstance();
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Chọn ngày:"));
        // Spinner date (chỉ ngày)
        SpinnerDateModel dateModel = new SpinnerDateModel(new Date(), null, null, java.util.Calendar.DAY_OF_MONTH);
        dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        top.add(dateSpinner);

        btnRefresh = new JButton("Làm mới");
        btnRefresh.addActionListener(e -> loadData());
        btnStart = new JButton("Khởi hành");
        btnStart.addActionListener(e -> startSelected());
        btnArrived = new JButton("Đến nơi");
        btnArrived.addActionListener(e -> arrivedSelected());

        top.add(btnRefresh);
        top.add(btnStart);
        top.add(btnArrived);

        add(top, BorderLayout.NORTH);

        // Added column "Ngày giờ chạy" before "Số vé (ngày)"
        String[] cols = {"Mã chuyến", "Mã tàu", "Ga đi", "Ga đến", "Ngày giờ chạy", "Số vé (ngày)", "Trạng thái tàu"};
        model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createTitledBorder("Danh sách chuyến"));
        add(sp, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JLabel lblNote = new JLabel("Chọn 1 chuyến rồi thao tác. Các thay đổi sẽ áp dụng cho ngày đã chọn.");
        bottom.add(lblNote, BorderLayout.WEST);
        add(bottom, BorderLayout.SOUTH);


        try { UIUtils.adjustTableForScale(table, 1.2f); } catch (Throwable ignored) {}

        loadData();
    }

    private LocalDate getSelectedDate() {
        Date d = (Date) dateSpinner.getValue();
        return Instant.ofEpochMilli(d.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Load chuyến chạy vào ngày đã chọn và tính số vé cho ngày đó.
     * Lưu ý: nếu chuyến là template (gioDi == null) thì kiểm tra vé trong ngày để quyết định hiển thị.
     */
    public void loadData() {
        btnRefresh.setEnabled(false);
        model.setRowCount(0);
        LocalDate date = getSelectedDate();

        SwingWorker<Void, Object[]> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                List<ChuyenTau> list = chuyenTauService.getAllChuyen();
                if (list == null) return null;
                for (ChuyenTau c : list) {
                    try {
                        LocalDateTime runTime = null;
                        boolean include = false;
                        if (c.getGioDi() != null) {
                            runTime = c.getGioDi();
                            if (runTime.toLocalDate().equals(date)) {
                                include = true;
                            } else {
                                include = false;
                            }
                        } else {
                            List<Ve> ves = veDAO.getByChuyenAndDate(c.getMaChuyen(), date);
                            if (ves != null && !ves.isEmpty()) {
                                include = true;
                                for (Ve v : ves) {
                                    if (v.getGioDi() != null) {
                                        runTime = v.getGioDi();
                                        break;
                                    }
                                }
                            } else {
                                include = false;
                            }
                        }

                        if (!include) continue;

                        int count = chuyenTauService.countTicketsForChuyenOnDate(c.getMaChuyen(), date);

                        String runTimeStr = runTime != null ? runTime.format(DT_FMT) : "";
                        String tauStatus = "";
                        tauStatus = chuyenTauService.getChuyenTauStatus(c.getMaChuyen());
                        publish(new Object[]{
                                c.getMaChuyen(),
                                c.getMaTau(),
                                c.getGaDi(),
                                c.getGaDen(),
                                c.getGioDi(),
                                count,
                                tauStatus
                        });
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void process(List<Object[]> chunks) {
                for (Object[] row : chunks) model.addRow(row);
            }

            @Override
            protected void done() {
                btnRefresh.setEnabled(true);
                try { UIUtils.adjustTableForScale(table, 1.2f); } catch (Throwable ignored) {}
            }
        };
        worker.execute();
    }

    private String getSelectedMaChuyen() {
        int r = table.getSelectedRow();
        if (r < 0) return null;
        return (String) model.getValueAt(r, 0);
    }

    private void startSelected() {
        String ma = getSelectedMaChuyen();
        if (ma == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến để khởi hành.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        LocalDate date = getSelectedDate();
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn đánh dấu chuyến " + ma + " tại ngày " + date + " là 'Đã khởi hành'?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        // run in background
        btnStart.setEnabled(false);
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    // user name could be fetched from session; using placeholder "UI"
                    chuyenTauService.startChuyenOnDate(ma, date, "UI");
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PnlChuyenTau.this, "Đã cập nhật: Khởi hành.", "Kết quả", JOptionPane.INFORMATION_MESSAGE));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PnlChuyenTau.this, "Lỗi khi khởi hành: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }

            @Override
            protected void done() {
                btnStart.setEnabled(true);
                loadData();
            }
        };
        w.execute();
    }

    private void arrivedSelected() {
        String ma = getSelectedMaChuyen();
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
        SwingWorker<Void, Void> w = new SwingWorker<>() {
            @Override
            protected Void doInBackground() {
                try {
                    int updated = chuyenTauService.arriveChuyenOnDate(ma, date, freePaid, "UI");
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PnlChuyenTau.this,
                            updated > 0 ? "Đã xử lý - ghế đã được giải phóng: " + updated : "Đã cập nhật trạng thái chuyến, nhưng không có ghế nào thay đổi.",
                            "Kết quả",
                            JOptionPane.INFORMATION_MESSAGE));
                } catch (Exception ex) {
                    ex.printStackTrace();
                    SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(PnlChuyenTau.this, "Lỗi khi xử lý: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }

            @Override
            protected void done() {
                btnArrived.setEnabled(true);
                loadData();
            }
        };
        w.execute();
    }
}