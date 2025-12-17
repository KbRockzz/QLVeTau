package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.model.*;
import com.trainstation.service.*;
import com.trainstation.dao.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel đổi vé
 */
public class PnlDoiVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private ChuyenTauDAO chuyenTauDAO;
    private ToaTauDAO toaTauDAO;
    private GheDAO gheDAO;
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private VeDAO veDAO;
    
    private JTextField txtTimKiem;
    private JComboBox<String> cmbTimKiemTheo;
    private JButton btnTimKiem;
    private JTable bangVe;
    private DefaultTableModel modelBangVe;
    private Ve veHienTai;

    public PnlDoiVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        this.chuyenTauDAO = ChuyenTauDAO.getInstance();
        this.toaTauDAO = ToaTauDAO.getInstance();
        this.gheDAO = GheDAO.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.veDAO = VeDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("ĐỔI VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Search panel
        JPanel pnlTimKiem = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlTimKiem.add(new JLabel("Tìm kiếm theo:"));
        
        cmbTimKiemTheo = new JComboBox<>(new String[]{"Số điện thoại", "Mã vé"});
        pnlTimKiem.add(cmbTimKiemTheo);
        
        txtTimKiem = new JTextField(20);
        pnlTimKiem.add(txtTimKiem);
        
        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.addActionListener(e -> timKiemVe());
        MaterialInitializer.styleButton(btnTimKiem);
        pnlTimKiem.add(btnTimKiem);
        
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(pnlTimKiem, BorderLayout.NORTH);
        
        // Ticket table
        String[] tenCot = {"Mã vé", "Chuyến", "Ga đi", "Ga đến", "Giờ đi", "Ghế", "Trạng thái"};
        modelBangVe = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangVe = new JTable(modelBangVe);
        JScrollPane scrollVe = new JScrollPane(bangVe);
        scrollVe.setBorder(BorderFactory.createTitledBorder("Danh sách vé"));
        // Giảm chiều cao bảng để có đủ không gian
        MaterialInitializer.setTableScrollPaneSize(scrollVe, 40);
        pnlTop.add(scrollVe, BorderLayout.CENTER);
        
        add(pnlTop, BorderLayout.CENTER);
        
        // Button panel - Material styled
        JPanel pnlButton = MaterialInitializer.createButtonPanel();
        JButton btnDoiVe = new JButton("Đổi vé");
        btnDoiVe.addActionListener(e -> chonVeDeDoiVe());
        MaterialInitializer.styleButton(btnDoiVe);
        pnlButton.add(btnDoiVe);
        
        add(pnlButton, BorderLayout.SOUTH);
    }

    private void timKiemVe() {
        String timKiem = txtTimKiem.getText().trim();
        if (timKiem.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập thông tin tìm kiếm!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        modelBangVe.setRowCount(0);
        
        String loaiTimKiem = (String) cmbTimKiemTheo.getSelectedItem();
        List<Ve> danhSachVe = new ArrayList<>();
        
        if ("Số điện thoại".equals(loaiTimKiem)) {
            // Tìm theo số điện thoại - lấy từ HoaDon
            List<HoaDon> danhSachHoaDon = hoaDonDAO.getAll().stream()
                .filter(hd -> timKiem.equals(hd.getSoDienThoai()))
                .collect(java.util.stream.Collectors.toList());
            
            for (HoaDon hoaDon : danhSachHoaDon) {
                List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hoaDon.getMaHoaDon());
                for (ChiTietHoaDon chiTiet : chiTietList) {
                    Ve ve = veDAO.findById(chiTiet.getMaVe());
                    if (ve != null && ve.isActive()) {
                        danhSachVe.add(ve);
                    }
                }
            }
            
            if (danhSachVe.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Không tìm thấy vé nào với số điện thoại: " + timKiem,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Tìm theo mã vé
            Ve ve = veDAO.findById(timKiem);
            if (ve != null && ve.isActive()) {
                danhSachVe.add(ve);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Không tìm thấy vé với mã: " + timKiem,
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (Ve ve : danhSachVe) {
            modelBangVe.addRow(new Object[]{
                ve.getMaVe(),
                ve.getMaChuyen(),
                ve.getTenGaDi(),
                ve.getTenGaDen(),
                ve.getGioDi() != null ? ve.getGioDi().format(formatter) : "",
                ve.getMaSoGhe(),
                ve.getTrangThai()
            });
        }
    }

    private void chonVeDeDoiVe() {
        int row = bangVe.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn vé cần đổi!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maVe = (String) modelBangVe.getValueAt(row, 0);
        veHienTai = veService.timVeTheoMa(maVe);
        
        if (veHienTai == null) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy vé!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Kiểm tra trạng thái vé trước khi mở dialog
        String trangThai = veHienTai.getTrangThai();
        if ("Đã hoàn".equals(trangThai) || "Đã hủy".equals(trangThai) || "Đã đổi".equals(trangThai)) {
            JOptionPane.showMessageDialog(this,
                "Không thể đổi vé có trạng thái: " + trangThai,
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Mở dialog đổi vé mới
        DlgDoiVe dialog = new DlgDoiVe((Frame) SwingUtilities.getWindowAncestor(this), veHienTai);
        dialog.setVisible(true);
        
        // Nếu đổi vé thành công, refresh lại danh sách
        if (dialog.isThanhCong()) {
            timKiemVe();
        }
    }
}
