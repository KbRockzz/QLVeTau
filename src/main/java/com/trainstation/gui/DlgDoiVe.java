package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.dao.GheDAO;
import com.trainstation.model.Ghe;
import com.trainstation.model.Ve;
import com.trainstation.service.VeService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Dialog đổi vé - chỉ cho phép đổi ghế trong cùng toa
 */
public class DlgDoiVe extends JDialog {
    private Ve veGoc;
    private GheDAO gheDAO;
    private VeService veService;
    
    private JTextField txtMaVeGoc;
    private JTextField txtChuyenGoc;
    private JTextField txtGaDiGoc;
    private JTextField txtGaDenGoc;
    private JTextField txtGioDiGoc;
    private JTextField txtGheGoc;
    private JTextField txtToaGoc;
    private JTextField txtTrangThaiGoc;
    
    private JPanel pnlSeatMap;
    private JTextArea txtLyDo;
    private String gheChon = null;
    
    private boolean thanhCong = false;
    
    // Modern color palette for seat states - Material Design inspired
    private static final Color COLOR_AVAILABLE = new Color(76, 175, 80);      // Material Green 500
    private static final Color COLOR_AVAILABLE_HOVER = new Color(102, 187, 106); // Material Green 400
    private static final Color COLOR_CURRENT = new Color(156, 39, 176);       // Material Purple 500
    private static final Color COLOR_SELECTED = new Color(33, 150, 243);      // Material Blue 500
    private static final Color COLOR_BOOKED = new Color(244, 67, 54);         // Material Red 500
    
    // Outline/border colors (darker shades for borders)
    private static final Color COLOR_CURRENT_BORDER = new Color(106, 27, 154);      // Darker purple (Purple 800)
    private static final Color COLOR_CURRENT_BORDER_ALPHA = new Color(106, 27, 154, 200); // Purple with alpha
    private static final Color COLOR_CURRENT_INNER_BORDER = new Color(255, 255, 255, 100); // White with alpha
    private static final Color COLOR_SELECTED_BORDER = new Color(21, 101, 192);    // Darker blue
    private static final Color COLOR_AVAILABLE_BORDER = new Color(56, 142, 60);    // Darker green
    private static final Color COLOR_BOOKED_BORDER = new Color(198, 40, 40);       // Darker red
    
    // Visual indicators (emoji icons)
    private static final String ICON_CURRENT = "🎯";
    private static final String ICON_AVAILABLE = "✓";
    private static final String ICON_BOOKED = "✕";
    
    public DlgDoiVe(Frame owner, Ve veGoc) {
        super(owner, "Đổi vé", true);
        this.veGoc = veGoc;
        this.gheDAO = GheDAO.getInstance();
        this.veService = VeService.getInstance();
        
        initComponents();
        loadThongTinVeGoc();
        loadSeatMap();
        
        setSize(1100, 700);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        // Main panel with border
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Center panel - split left and right
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        // Left panel - Thông tin vé gốc (read-only)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Thông tin vé hiện tại",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        txtMaVeGoc = createReadOnlyField();
        txtChuyenGoc = createReadOnlyField();
        txtGaDiGoc = createReadOnlyField();
        txtGaDenGoc = createReadOnlyField();
        txtGioDiGoc = createReadOnlyField();
        txtGheGoc = createReadOnlyField();
        txtToaGoc = createReadOnlyField();
        txtTrangThaiGoc = createReadOnlyField();
        
        formPanel.add(new JLabel("Mã vé:"));
        formPanel.add(txtMaVeGoc);
        formPanel.add(new JLabel("Chuyến:"));
        formPanel.add(txtChuyenGoc);
        formPanel.add(new JLabel("Ga đi:"));
        formPanel.add(txtGaDiGoc);
        formPanel.add(new JLabel("Ga đến:"));
        formPanel.add(txtGaDenGoc);
        formPanel.add(new JLabel("Giờ đi:"));
        formPanel.add(txtGioDiGoc);
        formPanel.add(new JLabel("Ghế hiện tại:"));
        formPanel.add(txtGheGoc);
        formPanel.add(new JLabel("Toa:"));
        formPanel.add(txtToaGoc);
        formPanel.add(new JLabel("Trạng thái:"));
        formPanel.add(txtTrangThaiGoc);
        
        leftPanel.add(formPanel, BorderLayout.NORTH);
        
        // Right panel - Seat map
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Chọn ghế mới (cùng toa)",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 12)
        ));
        
        // Seat map with scroll
        pnlSeatMap = new JPanel();
        pnlSeatMap.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollSeatMap = new JScrollPane(pnlSeatMap);
        scrollSeatMap.setPreferredSize(new Dimension(400, 300));
        rightPanel.add(scrollSeatMap, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(createLegendItem("Trống", COLOR_AVAILABLE));
        legendPanel.add(createLegendItem("Đã đặt", COLOR_BOOKED));
        legendPanel.add(createLegendItem("Hiện tại", COLOR_CURRENT));
        legendPanel.add(createLegendItem("Đang chọn", COLOR_SELECTED));
        rightPanel.add(legendPanel, BorderLayout.SOUTH);
        
        centerPanel.add(leftPanel);
        centerPanel.add(rightPanel);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Bottom panel - Reason and buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        
        JPanel reasonPanel = new JPanel(new BorderLayout(5, 5));
        reasonPanel.setBorder(BorderFactory.createTitledBorder("Lý do đổi vé"));
        txtLyDo = new JTextArea(3, 40);
        txtLyDo.setLineWrap(true);
        txtLyDo.setWrapStyleWord(true);
        JScrollPane scrollLyDo = new JScrollPane(txtLyDo);
        reasonPanel.add(scrollLyDo, BorderLayout.CENTER);
        bottomPanel.add(reasonPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        JButton btnXacNhan = new JButton("Xác nhận đổi vé");
        btnXacNhan.addActionListener(e -> xacNhanDoiVe());
        MaterialInitializer.styleButton(btnXacNhan);
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dispose());
        MaterialInitializer.styleButton(btnHuy);
        
        buttonPanel.add(btnXacNhan);
        buttonPanel.add(btnHuy);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JTextField createReadOnlyField() {
        JTextField field = new JTextField();
        field.setEditable(false);
        field.setBackground(new Color(240, 240, 240));
        return field;
    }
    
    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JLabel colorLabel = new JLabel("   ");
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.add(colorLabel);
        panel.add(new JLabel(text));
        return panel;
    }
    
    private void loadThongTinVeGoc() {
        if (veGoc == null) return;
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        txtMaVeGoc.setText(veGoc.getMaVe());
        txtChuyenGoc.setText(veGoc.getMaChuyen());
        txtGaDiGoc.setText(veGoc.getTenGaDi());
        txtGaDenGoc.setText(veGoc.getTenGaDen());
        txtGioDiGoc.setText(veGoc.getGioDi() != null ? veGoc.getGioDi().format(formatter) : "");
        txtGheGoc.setText(veGoc.getMaSoGhe());
        txtToaGoc.setText(veGoc.getSoToa() != null ? veGoc.getSoToa().toString() : "");
        txtTrangThaiGoc.setText(veGoc.getTrangThai());
    }
    
    private void loadSeatMap() {
        pnlSeatMap.removeAll();
        
        if (veGoc == null || veGoc.getMaSoGhe() == null) {
            pnlSeatMap.setLayout(new FlowLayout());
            pnlSeatMap.add(new JLabel("Không có thông tin ghế"));
            pnlSeatMap.revalidate();
            pnlSeatMap.repaint();
            return;
        }
        
        // Lấy thông tin ghế hiện tại để biết maToa
        Ghe gheHienTai = gheDAO.findById(veGoc.getMaSoGhe());
        if (gheHienTai == null) {
            pnlSeatMap.setLayout(new FlowLayout());
            pnlSeatMap.add(new JLabel("Không tìm thấy thông tin ghế"));
            pnlSeatMap.revalidate();
            pnlSeatMap.repaint();
            return;
        }
        
        String maToa = gheHienTai.getMaToa();
        
        // Load tất cả ghế trong cùng toa
        List<Ghe> danhSachGhe = gheDAO.getByToa(maToa);
        
        if (danhSachGhe.isEmpty()) {
            pnlSeatMap.setLayout(new FlowLayout());
            pnlSeatMap.add(new JLabel("Không có ghế trong toa này"));
        } else {
            // Modern seat map layout with better spacing: 2 ghế | lối đi | 2 ghế
            int soGhe = danhSachGhe.size();
            int soHang = (int) Math.ceil(soGhe / 4.0);
            
            pnlSeatMap.setLayout(new GridLayout(soHang, 5, 8, 8)); // Increased spacing for modern look
            
            for (int i = 0; i < soHang; i++) {
                // 2 ghế bên trái
                for (int j = 0; j < 2; j++) {
                    int index = i * 4 + j;
                    if (index < soGhe) {
                        pnlSeatMap.add(taoNutGhe(danhSachGhe.get(index)));
                    } else {
                        pnlSeatMap.add(new JLabel(""));
                    }
                }
                
                // Lối đi (aisle) - modern styling
                JPanel pnlLoiDi = new JPanel();
                pnlLoiDi.setBackground(new Color(224, 224, 224));
                pnlLoiDi.setPreferredSize(new Dimension(35, 45));
                pnlLoiDi.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)
                ));
                pnlSeatMap.add(pnlLoiDi);
                
                // 2 ghế bên phải
                for (int j = 2; j < 4; j++) {
                    int index = i * 4 + j;
                    if (index < soGhe) {
                        pnlSeatMap.add(taoNutGhe(danhSachGhe.get(index)));
                    } else {
                        pnlSeatMap.add(new JLabel(""));
                    }
                }
            }
        }
        
        pnlSeatMap.revalidate();
        pnlSeatMap.repaint();
    }
    
    /**
     * Tạo nút ghế với thiết kế hiện đại theo Material Design
     */
    private JButton taoNutGhe(Ghe ghe) {
        JButton btnGhe = new JButton(ghe.getMaGhe());
        
        // Modern styling with rounded corners
        btnGhe.setPreferredSize(new Dimension(85, 45));
        btnGhe.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGhe.setFocusPainted(false);
        btnGhe.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGhe.setOpaque(true);
        btnGhe.setContentAreaFilled(true);
        
        // Rounded border with FlatLaf properties for modern look
        btnGhe.putClientProperty("JButton.buttonType", "roundRect");
        btnGhe.putClientProperty("JComponent.roundRect", "6,6,6,6"); // 6px radius matching theme
        
        // Màu sắc theo trạng thái
        if (ghe.getMaGhe() != null && veGoc.getMaSoGhe() != null && 
            ghe.getMaGhe().equals(veGoc.getMaSoGhe())) {
            // Ghế hiện tại - màu tím với outline và shadow nổi bật
            styleSeatButton(btnGhe, COLOR_CURRENT, Color.WHITE, false, 
                ICON_CURRENT + " " + ghe.getMaGhe() + " - Ghế hiện tại");
            // Add prominent outline with shadow for current seat
            btnGhe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_CURRENT_BORDER_ALPHA, 2),
                    BorderFactory.createLineBorder(COLOR_CURRENT_INNER_BORDER, 1)
                ),
                BorderFactory.createEmptyBorder(2, 7, 2, 7)
            ));
            // Enhanced shadow for current seat (purple glow)
            btnGhe.putClientProperty("FlatLaf.style", "shadowColor: rgba(156,39,176,102); shadowWidth: 5");
        } else if ("Rảnh".equalsIgnoreCase(ghe.getTrangThai()) || "Trống".equalsIgnoreCase(ghe.getTrangThai())) {
            // Ghế trống - màu xanh với outline, shadow và hover effect
            final String maGhe = ghe.getMaGhe();
            
            // Check if this is the selected seat
            if (maGhe.equals(gheChon)) {
                styleSeatButton(btnGhe, COLOR_SELECTED, Color.WHITE, true, 
                    ICON_AVAILABLE + " " + ghe.getMaGhe() + " - Đang chọn");
                // Outline for selected seat
                btnGhe.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_SELECTED_BORDER, 2),
                    BorderFactory.createEmptyBorder(3, 8, 3, 8)
                ));
                // Enhanced shadow for selected seat
                btnGhe.putClientProperty("FlatLaf.style", "shadowColor: rgba(33,150,243,89); shadowWidth: 4");
            } else {
                styleSeatButton(btnGhe, COLOR_AVAILABLE, Color.WHITE, true, 
                    ICON_AVAILABLE + " " + ghe.getMaGhe() + " - Trống");
                // Subtle outline and shadow for available seats
                btnGhe.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_AVAILABLE_BORDER, 1),
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
                ));
                btnGhe.putClientProperty("FlatLaf.style", "shadowColor: rgba(0,0,0,64); shadowWidth: 3");
                
                // Add modern hover effect - shared listener
                btnGhe.addMouseListener(createHoverListener(btnGhe, maGhe));
            }
            
            btnGhe.addActionListener(e -> {
                gheChon = maGhe;
                updateSeatColors();
            });
        } else {
            // Ghế đã đặt - màu đỏ với outline và shadow
            styleSeatButton(btnGhe, COLOR_BOOKED, Color.WHITE, false, 
                ICON_BOOKED + " " + ghe.getMaGhe() + " - Đã đặt");
            // Outline for booked seats
            btnGhe.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BOOKED_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 9, 4, 9)
            ));
            btnGhe.putClientProperty("FlatLaf.style", "shadowColor: rgba(0,0,0,64); shadowWidth: 3");
        }
        
        return btnGhe;
    }
    
    /**
     * Tạo hover listener cho ghế trống (reusable)
     */
    private java.awt.event.MouseAdapter createHoverListener(JButton btn, String maGhe) {
        return new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled() && !maGhe.equals(gheChon)) {
                    btn.setBackground(COLOR_AVAILABLE_HOVER);
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn.isEnabled() && !maGhe.equals(gheChon)) {
                    btn.setBackground(COLOR_AVAILABLE);
                }
            }
        };
    }
    
    /**
     * Áp dụng styling hiện đại cho nút ghế
     */
    private void styleSeatButton(JButton btn, Color bgColor, Color fgColor, boolean enabled, String tooltip) {
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setEnabled(enabled);
        btn.setToolTipText(tooltip);
        
        // FlatLaf properties to preserve colors when disabled
        if (!enabled) {
            btn.putClientProperty("Button.disabledBackground", bgColor);
            btn.putClientProperty("Button.disabledText", fgColor);
        }
    }
    
    /**
     * Update seat button colors without full reconstruction (more efficient)
     */
    private void updateSeatColors() {
        Component[] components = pnlSeatMap.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String btnText = btn.getText();
                
                // Extract seat code (button text is just the seat code, no emoji prefix)
                String maGhe = btnText;
                
                // Skip current seat (always cyan) and disabled seats
                if (maGhe.equals(veGoc.getMaSoGhe()) || !btn.isEnabled()) {
                    continue;
                }
                
                // Update color, border, and shadow based on selection
                if (maGhe.equals(gheChon)) {
                    btn.setBackground(COLOR_SELECTED);
                    btn.setForeground(Color.WHITE);
                    btn.setToolTipText(ICON_AVAILABLE + " " + maGhe + " - Đang chọn");
                    // Add outline and enhanced shadow for selected
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_SELECTED_BORDER, 2),
                        BorderFactory.createEmptyBorder(3, 8, 3, 8)
                    ));
                    btn.putClientProperty("FlatLaf.style", "shadowColor: rgba(33,150,243,89); shadowWidth: 4");
                } else {
                    // Reset to green for available seats
                    btn.setBackground(COLOR_AVAILABLE);
                    btn.setForeground(Color.WHITE);
                    btn.setToolTipText(ICON_AVAILABLE + " " + maGhe + " - Trống");
                    // Reset to subtle outline and shadow
                    btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_AVAILABLE_BORDER, 1),
                        BorderFactory.createEmptyBorder(4, 9, 4, 9)
                    ));
                    btn.putClientProperty("FlatLaf.style", "shadowColor: rgba(0,0,0,64); shadowWidth: 3");
                }
            }
        }
        pnlSeatMap.repaint();
    }

    private void xacNhanDoiVe() {
        if (gheChon == null || gheChon.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn ghế mới trước khi xác nhận.",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String lyDo = txtLyDo.getText().trim();

        // Disable nút để tránh bấm nhiều lần
        setEnabledButtons(false);

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private Exception error;

            @Override
            protected Void doInBackground() {
                System.out.println("DEBUG DlgDoiVe: doInBackground start");
                try {
                    veService.thucHienDoiVe(veGoc.getMaVe(), gheChon, lyDo);
                } catch (Exception ex) {
                    error = ex;
                }
                System.out.println("DEBUG DlgDoiVe: doInBackground end");
                return null;
            }

            @Override
            protected void done() {
                setEnabledButtons(true);
                if (error != null) {
                    error.printStackTrace();
                    JOptionPane.showMessageDialog(DlgDoiVe.this,
                            "Lỗi khi đổi vé: " + error.getMessage(),
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    thanhCong = true;
                    JOptionPane.showMessageDialog(DlgDoiVe.this,
                            "Đổi vé thành công!",
                            "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                }
            }
        };
        worker.execute();
    }

    private void setEnabledButtons(boolean enabled) {
        // Tìm 2 nút trong bottomPanel và bật/tắt
        // Hoặc lưu field btnXacNhan / btnHuy trong class và setEnabled(enabled)
    }
    
    public boolean isThanhCong() {
        return thanhCong;
    }
}
