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
        legendPanel.add(createLegendItem("Trống", new Color(34, 139, 34)));
        legendPanel.add(createLegendItem("Đã đặt", Color.RED));
        legendPanel.add(createLegendItem("Đang chọn", Color.BLUE));
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
            // Sử dụng layout giống PnlDatVe: 2 ghế | lối đi | 2 ghế
            int soGhe = danhSachGhe.size();
            int soHang = (int) Math.ceil(soGhe / 4.0);
            
            pnlSeatMap.setLayout(new GridLayout(soHang, 5, 5, 5));
            
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
                
                // Lối đi (aisle)
                JPanel pnlLoiDi = new JPanel();
                pnlLoiDi.setBackground(new Color(200, 200, 200));
                pnlLoiDi.setPreferredSize(new Dimension(30, 40));
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
     * Tạo nút ghế với màu sắc tương ứng
     */
    private JButton taoNutGhe(Ghe ghe) {
        JButton btnGhe = new JButton(ghe.getMaGhe());
        btnGhe.setPreferredSize(new Dimension(80, 40));
        btnGhe.setFont(new Font("Arial", Font.BOLD, 12));
        btnGhe.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        // Màu sắc theo trạng thái
        if (ghe.getMaGhe().equals(veGoc.getMaSoGhe())) {
            // Ghế hiện tại - màu đỏ giống ghế đã đặt (không thể chọn)
            btnGhe.setBackground(Color.RED);
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(false);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Ghế hiện tại");
        } else if ("Rảnh".equalsIgnoreCase(ghe.getTrangThai()) || "Trống".equalsIgnoreCase(ghe.getTrangThai())) {
            // Ghế trống - màu xanh (check both "Rảnh" and "Trống" for compatibility)
            final String maGhe = ghe.getMaGhe();
            btnGhe.setBackground(new Color(34, 139, 34));
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(true);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Trống");
            
            // Check if this is the selected seat
            if (maGhe.equals(gheChon)) {
                btnGhe.setBackground(Color.BLUE);
                btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Đang chọn");
            }
            
            btnGhe.addActionListener(e -> {
                gheChon = maGhe;
                updateSeatColors();
            });
        } else {
            // Ghế đã đặt - màu đỏ
            btnGhe.setBackground(Color.RED);
            btnGhe.setForeground(Color.BLACK);
            btnGhe.setEnabled(false);
            btnGhe.setToolTipText("Ghế " + ghe.getMaGhe() + " - Đã đặt");
        }
        
        return btnGhe;
    }
    
    /**
     * Update seat button colors without full reconstruction (more efficient)
     */
    private void updateSeatColors() {
        Component[] components = pnlSeatMap.getComponents();
        for (Component comp : components) {
            if (comp instanceof JButton) {
                JButton btn = (JButton) comp;
                String maGhe = btn.getText();
                
                // Skip current seat (always orange) and disabled seats
                if (maGhe.equals(veGoc.getMaSoGhe()) || !btn.isEnabled()) {
                    continue;
                }
                
                // Update color based on selection
                if (maGhe.equals(gheChon)) {
                    btn.setBackground(Color.BLUE);
                    btn.setToolTipText("Ghế " + maGhe + " - Đang chọn");
                } else {
                    // Reset to green for available seats
                    btn.setBackground(new Color(34, 139, 34));
                    btn.setToolTipText("Ghế " + maGhe + " - Trống");
                }
            }
        }
        pnlSeatMap.repaint();
    }
    
    private void xacNhanDoiVe() {
        // Validate ghế đã chọn
        if (gheChon == null) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn ghế mới!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate lý do
        String lyDo = txtLyDo.getText().trim();
        if (lyDo.isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn chưa nhập lý do đổi vé. Có muốn tiếp tục?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        // Confirm
        int confirm = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc muốn đổi vé từ ghế " + veGoc.getMaSoGhe() + " sang ghế " + gheChon + "?",
            "Xác nhận đổi vé",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Thực hiện đổi vé
        try {
            Ve veMoi = veService.thucHienDoiVe(veGoc.getMaVe(), gheChon, lyDo);
            
            JOptionPane.showMessageDialog(this,
                "Đổi vé thành công!\nMã vé mới: " + veMoi.getMaVe(),
                "Thành công",
                JOptionPane.INFORMATION_MESSAGE);
            
            thanhCong = true;
            dispose();
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi không xác định: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    public boolean isThanhCong() {
        return thanhCong;
    }
}
