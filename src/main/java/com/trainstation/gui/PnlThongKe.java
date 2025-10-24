package com.trainstation.gui;

import com.trainstation.service.ThongKeService;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;
import java.text.DecimalFormat;

/**
 * Panel thống kê với 3 chức năng:
 * 1. Thống kê doanh thu
 * 2. Thống kê vé hoàn/đổi
 * 3. Thống kê độ phủ ghế
 */
public class PnlThongKe extends JPanel {
    private ThongKeService thongKeService;
    
    // Navigation buttons
    private JButton btnDoanhThu, btnVeDoiHoan, btnDoPhuGhe;
    
    // CardLayout and main panel
    private CardLayout cardLayout;
    private JPanel pnlMain;
    
    // Revenue statistics panel components
    private JDateChooser dateDoanhThuTu, dateDoanhThuDen;
    private JButton btnThongKeDoanhThu;
    private JTable tblDoanhThu;
    private DefaultTableModel modelDoanhThu;
    private JLabel lblTongDoanhThu;
    
    // Ticket refund/exchange panel components
    private JDateChooser dateVeDoiHoanTu, dateVeDoiHoanDen;
    private JButton btnThongKeVeDoiHoan;
    private JTable tblVeDoiHoan;
    private DefaultTableModel modelVeDoiHoan;
    private JLabel lblTongVeDoiHoan;
    
    // Seat coverage panel components
    private JDateChooser dateDoPhuGheTu, dateDoPhuGheDen;
    private JButton btnThongKeDoPhuGhe;
    private JTable tblDoPhuGhe;
    private DefaultTableModel modelDoPhuGhe;
    private JLabel lblTongDoPhuGhe;
    
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0");
    private DecimalFormat percentFormat = new DecimalFormat("#,##0.00");

    public PnlThongKe() {
        this.thongKeService = ThongKeService.getInstance();
        initComponents();
        loadDefaultData();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("THỐNG KÊ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Navigation buttons panel
        JPanel pnlNav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        
        btnDoanhThu = new JButton("Thống kê doanh thu");
        btnDoanhThu.setPreferredSize(new Dimension(200, 40));
        btnDoanhThu.setFont(new Font("Arial", Font.BOLD, 14));
        btnDoanhThu.addActionListener(e -> showPanel("doanhThu"));
        
        btnVeDoiHoan = new JButton("Thống kê vé hoàn/đổi");
        btnVeDoiHoan.setPreferredSize(new Dimension(200, 40));
        btnVeDoiHoan.setFont(new Font("Arial", Font.BOLD, 14));
        btnVeDoiHoan.addActionListener(e -> showPanel("veDoiHoan"));
        
        btnDoPhuGhe = new JButton("Thống kê độ phủ ghế");
        btnDoPhuGhe.setPreferredSize(new Dimension(200, 40));
        btnDoPhuGhe.setFont(new Font("Arial", Font.BOLD, 14));
        btnDoPhuGhe.addActionListener(e -> showPanel("doPhuGhe"));
        
        pnlNav.add(btnDoanhThu);
        pnlNav.add(btnVeDoiHoan);
        pnlNav.add(btnDoPhuGhe);

        // Main panel with CardLayout
        cardLayout = new CardLayout();
        pnlMain = new JPanel(cardLayout);
        
        pnlMain.add(createDoanhThuPanel(), "doanhThu");
        pnlMain.add(createVeDoiHoanPanel(), "veDoiHoan");
        pnlMain.add(createDoPhuGhePanel(), "doPhuGhe");

        // Add panels to main layout
        JPanel pnlCenter = new JPanel(new BorderLayout());
        pnlCenter.add(pnlNav, BorderLayout.NORTH);
        pnlCenter.add(pnlMain, BorderLayout.CENTER);
        
        add(pnlCenter, BorderLayout.CENTER);
    }

    /**
     * Create Revenue Statistics Panel
     */
    private JPanel createDoanhThuPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.add(new JLabel("Từ ngày:"));
        dateDoanhThuTu = new JDateChooser();
        dateDoanhThuTu.setPreferredSize(new Dimension(150, 25));
        dateDoanhThuTu.setDateFormatString("dd/MM/yyyy");
        pnlFilter.add(dateDoanhThuTu);
        
        pnlFilter.add(new JLabel("Đến ngày:"));
        dateDoanhThuDen = new JDateChooser();
        dateDoanhThuDen.setPreferredSize(new Dimension(150, 25));
        dateDoanhThuDen.setDateFormatString("dd/MM/yyyy");
        pnlFilter.add(dateDoanhThuDen);
        
        btnThongKeDoanhThu = new JButton("Thống kê");
        btnThongKeDoanhThu.addActionListener(e -> loadDoanhThuData());
        pnlFilter.add(btnThongKeDoanhThu);
        
        panel.add(pnlFilter, BorderLayout.NORTH);

        // Table
        String[] columns = {"Ngày bán", "Tổng doanh thu (VNĐ)"};
        modelDoanhThu = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDoanhThu = new JTable(modelDoanhThu);
        tblDoanhThu.setRowHeight(25);
        tblDoanhThu.getColumnModel().getColumn(0).setPreferredWidth(200);
        tblDoanhThu.getColumnModel().getColumn(1).setPreferredWidth(300);
        
        JScrollPane scrollPane = new JScrollPane(tblDoanhThu);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        lblTongDoanhThu = new JLabel("Tổng cộng: 0 VNĐ");
        lblTongDoanhThu.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongDoanhThu.setForeground(new Color(0, 128, 0));
        pnlSummary.add(lblTongDoanhThu);
        
        panel.add(pnlSummary, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create Ticket Refund/Exchange Statistics Panel
     */
    private JPanel createVeDoiHoanPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.add(new JLabel("Từ ngày:"));
        dateVeDoiHoanTu = new JDateChooser();
        dateVeDoiHoanTu.setPreferredSize(new Dimension(150, 25));
        dateVeDoiHoanTu.setDateFormatString("dd/MM/yyyy");
        pnlFilter.add(dateVeDoiHoanTu);
        
        pnlFilter.add(new JLabel("Đến ngày:"));
        dateVeDoiHoanDen = new JDateChooser();
        dateVeDoiHoanDen.setPreferredSize(new Dimension(150, 25));
        dateVeDoiHoanDen.setDateFormatString("dd/MM/yyyy");
        pnlFilter.add(dateVeDoiHoanDen);
        
        btnThongKeVeDoiHoan = new JButton("Thống kê");
        btnThongKeVeDoiHoan.addActionListener(e -> loadVeDoiHoanData());
        pnlFilter.add(btnThongKeVeDoiHoan);
        
        panel.add(pnlFilter, BorderLayout.NORTH);

        // Table
        String[] columns = {"Mã vé", "Mã hóa đơn", "Ngày giao dịch", "Hình thức", "Trạng thái"};
        modelVeDoiHoan = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblVeDoiHoan = new JTable(modelVeDoiHoan);
        tblVeDoiHoan.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tblVeDoiHoan);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        lblTongVeDoiHoan = new JLabel("Tổng số: 0 vé");
        lblTongVeDoiHoan.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongVeDoiHoan.setForeground(new Color(255, 140, 0));
        pnlSummary.add(lblTongVeDoiHoan);
        
        panel.add(pnlSummary, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Create Seat Coverage Statistics Panel
     */
    private JPanel createDoPhuGhePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter panel
        JPanel pnlFilter = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlFilter.add(new JLabel("Từ ngày:"));
        dateDoPhuGheTu = new JDateChooser();
        dateDoPhuGheTu.setPreferredSize(new Dimension(150, 25));
        dateDoPhuGheTu.setDateFormatString("dd/MM/yyyy");
        pnlFilter.add(dateDoPhuGheTu);
        
        pnlFilter.add(new JLabel("Đến ngày:"));
        dateDoPhuGheDen = new JDateChooser();
        dateDoPhuGheDen.setPreferredSize(new Dimension(150, 25));
        dateDoPhuGheDen.setDateFormatString("dd/MM/yyyy");
        pnlFilter.add(dateDoPhuGheDen);
        
        btnThongKeDoPhuGhe = new JButton("Thống kê");
        btnThongKeDoPhuGhe.addActionListener(e -> loadDoPhuGheData());
        pnlFilter.add(btnThongKeDoPhuGhe);
        
        panel.add(pnlFilter, BorderLayout.NORTH);

        // Table
        String[] columns = {"Ngày", "Tổng số vé bán", "Tổng số ghế có sẵn", "Tỷ lệ phủ (%)"};
        modelDoPhuGhe = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblDoPhuGhe = new JTable(modelDoPhuGhe);
        tblDoPhuGhe.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(tblDoPhuGhe);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Summary panel
        JPanel pnlSummary = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        lblTongDoPhuGhe = new JLabel("Tỷ lệ trung bình: 0.00%");
        lblTongDoPhuGhe.setFont(new Font("Arial", Font.BOLD, 16));
        lblTongDoPhuGhe.setForeground(new Color(0, 0, 255));
        pnlSummary.add(lblTongDoPhuGhe);
        
        panel.add(pnlSummary, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Show specific panel
     */
    private void showPanel(String panelName) {
        cardLayout.show(pnlMain, panelName);
        
        // Update button styles
        btnDoanhThu.setBackground(panelName.equals("doanhThu") ? new Color(70, 130, 180) : null);
        btnDoanhThu.setForeground(panelName.equals("doanhThu") ? Color.WHITE : Color.BLACK);
        
        btnVeDoiHoan.setBackground(panelName.equals("veDoiHoan") ? new Color(70, 130, 180) : null);
        btnVeDoiHoan.setForeground(panelName.equals("veDoiHoan") ? Color.WHITE : Color.BLACK);
        
        btnDoPhuGhe.setBackground(panelName.equals("doPhuGhe") ? new Color(70, 130, 180) : null);
        btnDoPhuGhe.setForeground(panelName.equals("doPhuGhe") ? Color.WHITE : Color.BLACK);
    }

    /**
     * Load default data for current month
     */
    private void loadDefaultData() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        
        // Set default dates for all panels
        setDateRange(dateDoanhThuTu, dateDoanhThuDen, firstDayOfMonth, lastDayOfMonth);
        setDateRange(dateVeDoiHoanTu, dateVeDoiHoanDen, firstDayOfMonth, lastDayOfMonth);
        setDateRange(dateDoPhuGheTu, dateDoPhuGheDen, firstDayOfMonth, lastDayOfMonth);
        
        // Load revenue data by default
        showPanel("doanhThu");
        loadDoanhThuData();
    }

    /**
     * Helper method to set date range
     */
    private void setDateRange(JDateChooser from, JDateChooser to, LocalDate fromDate, LocalDate toDate) {
        from.setDate(java.sql.Date.valueOf(fromDate));
        to.setDate(java.sql.Date.valueOf(toDate));
    }

    /**
     * Load revenue data
     */
    private void loadDoanhThuData() {
        try {
            modelDoanhThu.setRowCount(0);
            
            LocalDate tuNgay = dateDoanhThuTu.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate denNgay = dateDoanhThuDen.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            
            Map<String, Double> data = thongKeService.thongKeDoanhThu(tuNgay, denNgay);
            
            double tongDoanhThu = 0;
            
            // Sort by date
            List<String> sortedKeys = new ArrayList<>(data.keySet());
            Collections.sort(sortedKeys);
            
            for (String ngay : sortedKeys) {
                double doanhThu = data.get(ngay);
                tongDoanhThu += doanhThu;
                modelDoanhThu.addRow(new Object[]{ngay, currencyFormat.format(doanhThu)});
            }
            
            // Update total
            lblTongDoanhThu.setText("Tổng cộng: " + currencyFormat.format(tongDoanhThu) + " VNĐ");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load ticket refund/exchange data
     */
    private void loadVeDoiHoanData() {
        try {
            modelVeDoiHoan.setRowCount(0);
            
            LocalDate tuNgay = dateVeDoiHoanTu.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate denNgay = dateVeDoiHoanDen.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            
            List<Map<String, Object>> data = thongKeService.thongKeVeDoiHoan(tuNgay, denNgay);
            
            for (Map<String, Object> row : data) {
                modelVeDoiHoan.addRow(new Object[]{
                    row.get("maVe"),
                    row.get("maHoaDon"),
                    row.get("ngayGiaoDich"),
                    row.get("hinhThuc"),
                    row.get("trangThai")
                });
            }
            
            // Update total
            lblTongVeDoiHoan.setText("Tổng số: " + data.size() + " vé");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Load seat coverage data
     */
    private void loadDoPhuGheData() {
        try {
            modelDoPhuGhe.setRowCount(0);
            
            LocalDate tuNgay = dateDoPhuGheTu.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate denNgay = dateDoPhuGheDen.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();
            
            List<Map<String, Object>> data = thongKeService.thongKeDoPhuGhe(tuNgay, denNgay);
            
            double tongTyLe = 0;
            int count = 0;
            
            for (Map<String, Object> row : data) {
                double tyLePhu = (Double) row.get("tyLePhu");
                tongTyLe += tyLePhu;
                count++;
                
                modelDoPhuGhe.addRow(new Object[]{
                    row.get("ngay"),
                    row.get("soVeBan"),
                    row.get("tongSoGhe"),
                    percentFormat.format(tyLePhu) + "%"
                });
            }
            
            // Update average
            double tyLeTrungBinh = count > 0 ? tongTyLe / count : 0;
            lblTongDoPhuGhe.setText("Tỷ lệ trung bình: " + percentFormat.format(tyLeTrungBinh) + "%");
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi khi tải dữ liệu: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}
