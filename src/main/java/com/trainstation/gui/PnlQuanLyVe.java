package com.trainstation.gui;

import com.trainstation.dao.ChiTietHoaDonDAO;
import com.trainstation.dao.HoaDonDAO;
import com.trainstation.dao.KhachHangDAO;
import com.trainstation.model.*;
import com.trainstation.service.HoaDonService;
import com.trainstation.service.VeService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Panel quản lý vé và hóa đơn
 */
public class PnlQuanLyVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;
    private HoaDonService hoaDonService;
    private HoaDonDAO hoaDonDAO;
    private ChiTietHoaDonDAO chiTietHoaDonDAO;
    private KhachHangDAO khachHangDAO;
    
    private JTable bangHoaDon;
    private DefaultTableModel modelBangHoaDon;
    private JButton btnXuatHoaDon;
    private JButton btnTaiLai;

    public PnlQuanLyVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        this.hoaDonService = HoaDonService.getInstance();
        this.hoaDonDAO = HoaDonDAO.getInstance();
        this.chiTietHoaDonDAO = ChiTietHoaDonDAO.getInstance();
        this.khachHangDAO = KhachHangDAO.getInstance();
        initComponents();
        taiDanhSachHoaDon();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("QUẢN LÝ VÉ VÀ HÓA ĐƠN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Invoice table
        String[] tenCot = {"Mã hóa đơn", "Mã KH", "Tên KH", "Ngày lập", "PT thanh toán", "Trạng thái", "Số vé"};
        modelBangHoaDon = new DefaultTableModel(tenCot, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bangHoaDon = new JTable(modelBangHoaDon);
        JScrollPane scrollPane = new JScrollPane(bangHoaDon);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Danh sách hóa đơn"));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel pnlButton = new JPanel(new FlowLayout());
        
        btnXuatHoaDon = new JButton("Xuất hóa đơn");
        btnXuatHoaDon.addActionListener(e -> xuatHoaDon());
        pnlButton.add(btnXuatHoaDon);
        
        btnTaiLai = new JButton("Tải lại");
        btnTaiLai.addActionListener(e -> taiDanhSachHoaDon());
        pnlButton.add(btnTaiLai);
        
        add(pnlButton, BorderLayout.SOUTH);
    }

    private void taiDanhSachHoaDon() {
        modelBangHoaDon.setRowCount(0);
        List<HoaDon> danhSachHoaDon = hoaDonDAO.getAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        for (HoaDon hd : danhSachHoaDon) {
            KhachHang kh = khachHangDAO.findById(hd.getMaKH());
            List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hd.getMaHoaDon());
            
            modelBangHoaDon.addRow(new Object[]{
                hd.getMaHoaDon(),
                hd.getMaKH(),
                kh != null ? kh.getTenKhachHang() : "N/A",
                hd.getNgayLap() != null ? hd.getNgayLap().format(formatter) : "Chưa xác nhận",
                hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan() : "N/A",
                hd.getTrangThai(),
                chiTietList.size()
            });
        }
    }

    private void xuatHoaDon() {
        int row = bangHoaDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn cần xuất!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String maHoaDon = (String) modelBangHoaDon.getValueAt(row, 0);
        String trangThai = (String) modelBangHoaDon.getValueAt(row, 5);
        
        HoaDon hoaDon = hoaDonDAO.findById(maHoaDon);
        if (hoaDon == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // If invoice is not confirmed yet, show confirmation dialog
        if ("Chờ xác nhận".equals(trangThai)) {
            xacNhanXuatHoaDon(hoaDon);
        } else {
            // Already confirmed, just export PDF
            try {
                String fileName = hoaDonService.xuatHoaDonPDF(maHoaDon);
                
                // Also print all tickets in this invoice
                List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(maHoaDon);
                for (ChiTietHoaDon ct : chiTietList) {
                    Ve ve = veService.timVeTheoMa(ct.getMaVe());
                    if (ve != null) {
                        veService.inVePDF(ve);
                    }
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Xuất hóa đơn và vé thành công!\nFile hóa đơn: " + fileName, 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi khi xuất PDF: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void xacNhanXuatHoaDon(HoaDon hoaDon) {
        KhachHang kh = khachHangDAO.findById(hoaDon.getMaKH());
        
        // Create confirmation dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận xuất hóa đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel pnlNoiDung = new JPanel(new GridBagLayout());
        pnlNoiDung.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Message
        String message = String.format(
            "Xác nhận xuất hóa đơn %s cho khách hàng %s.\nVui lòng chọn phương thức thanh toán:",
            hoaDon.getMaHoaDon(),
            kh != null ? kh.getTenKhachHang() : "N/A"
        );
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JTextArea txtMessage = new JTextArea(message);
        txtMessage.setEditable(false);
        txtMessage.setOpaque(false);
        txtMessage.setWrapStyleWord(true);
        txtMessage.setLineWrap(true);
        pnlNoiDung.add(txtMessage, gbc);
        
        // Payment method selection
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        pnlNoiDung.add(new JLabel("Phương thức thanh toán:"), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        JComboBox<String> cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        pnlNoiDung.add(cboPhuongThuc, gbc);
        
        dialog.add(pnlNoiDung, BorderLayout.CENTER);
        
        // Buttons
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.addActionListener(e -> {
            try {
                // Update invoice
                hoaDon.setTrangThai("Hoàn tất");
                hoaDon.setNgayLap(LocalDateTime.now());
                hoaDon.setPhuongThucThanhToan((String) cboPhuongThuc.getSelectedItem());
                hoaDonDAO.update(hoaDon);
                
                // Export invoice PDF
                String fileName = hoaDonService.xuatHoaDonPDF(hoaDon.getMaHoaDon());
                
                // Print all tickets
                List<ChiTietHoaDon> chiTietList = chiTietHoaDonDAO.findByHoaDon(hoaDon.getMaHoaDon());
                for (ChiTietHoaDon ct : chiTietList) {
                    Ve ve = veService.timVeTheoMa(ct.getMaVe());
                    if (ve != null) {
                        veService.inVePDF(ve);
                    }
                }
                
                JOptionPane.showMessageDialog(dialog, 
                    "Xuất hóa đơn và vé thành công!\nFile hóa đơn: " + fileName, 
                    "Thành công", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                taiDanhSachHoaDon();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Lỗi khi xuất hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
        
        JButton btnHuy = new JButton("Hủy");
        btnHuy.addActionListener(e -> dialog.dispose());
        
        pnlButton.add(btnXacNhan);
        pnlButton.add(btnHuy);
        dialog.add(pnlButton, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}
