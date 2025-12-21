package com.trainstation.gui;

import com.trainstation.config.MaterialInitializer;
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
 * Xuất hóa đơn
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
    private JButton btnXemChiTiet;

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

        JLabel lblTieuDe = new JLabel("XUẤT HÓA ĐƠN", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

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
        MaterialInitializer.setTableScrollPaneSize(scrollPane, 45);
        add(scrollPane, BorderLayout.CENTER);

        JPanel pnlButton = MaterialInitializer.createButtonPanel();

        btnXuatHoaDon = new JButton("Xuất hóa đơn");
        btnXuatHoaDon.addActionListener(e -> xuatHoaDon());
        MaterialInitializer.styleButton(btnXuatHoaDon);
        pnlButton.add(btnXuatHoaDon);

        btnXemChiTiet = new JButton("Xem chi tiết");
        btnXemChiTiet.addActionListener(e -> xemChiTietHoaDon());
        MaterialInitializer.styleButton(btnXemChiTiet);
        pnlButton.add(btnXemChiTiet);

        btnTaiLai = new JButton("Tải lại");
        btnTaiLai.addActionListener(e -> taiDanhSachHoaDon());
        MaterialInitializer.styleButton(btnTaiLai);
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

        
        if ("Chờ xác nhận".equals(trangThai)) {
            xacNhanXuatHoaDon(hoaDon);
        } else {
            
            try {
                String fileName = hoaDonService.xuatHoaDonPDF(maHoaDon);

                
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

        
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Xác nhận xuất hóa đơn", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(450, 250);
        dialog.setLocationRelativeTo(this);

        JPanel pnlNoiDung = new JPanel(new GridBagLayout());
        pnlNoiDung.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        
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

        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        pnlNoiDung.add(new JLabel("Phương thức thanh toán:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        JComboBox<String> cboPhuongThuc = new JComboBox<>(new String[]{"Tiền mặt", "Chuyển khoản"});
        pnlNoiDung.add(cboPhuongThuc, gbc);

        dialog.add(pnlNoiDung, BorderLayout.CENTER);

        
        JPanel pnlButton = new JPanel(new FlowLayout());
        JButton btnXacNhan = new JButton("Xác nhận");
        btnXacNhan.addActionListener(e -> {
            try {
                
                hoaDon.setTrangThai("Hoàn tất");
                hoaDon.setNgayLap(LocalDateTime.now());
                hoaDon.setPhuongThucThanhToan((String) cboPhuongThuc.getSelectedItem());
                hoaDonDAO.update(hoaDon);

                
                String fileName = hoaDonService.xuatHoaDonPDF(hoaDon.getMaHoaDon());

                
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

    

    private void xemChiTietHoaDon() {
        int row = bangHoaDon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn hóa đơn để xem chi tiết.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String maHoaDon = (String) modelBangHoaDon.getValueAt(row, 0);

        // Load chi tiết bằng SwingWorker để không block UI
        SwingWorker<List<ChiTietHoaDon>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ChiTietHoaDon> doInBackground() {
                return chiTietHoaDonDAO.findByHoaDon(maHoaDon);
            }

            @Override
            protected void done() {
                try {
                    List<ChiTietHoaDon> items = get();
                    if (items == null || items.isEmpty()) {
                        JOptionPane.showMessageDialog(PnlQuanLyVe.this, "Hóa đơn chưa có chi tiết.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    
                    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(PnlQuanLyVe.this), "Chi tiết hóa đơn: " + maHoaDon, true);
                    dialog.setLayout(new BorderLayout(8,8));
                    dialog.setSize(700, 400);
                    dialog.setLocationRelativeTo(PnlQuanLyVe.this);

                    String[] cols = {"Mã vé", "Mã loại vé", "Mã chuyến", "Mã ghế", "Giá gốc", "Giá sau KM", "Ghi chú"};
                    DefaultTableModel m = new DefaultTableModel(cols, 0) {
                        @Override public boolean isCellEditable(int r, int c) { return false; }
                    };
                    JTable tbl = new JTable(m);

                    float total = 0f;
                    for (ChiTietHoaDon ct : items) {
                        
                        Ve ve = null;
                        try { ve = veService.timVeTheoMa(ct.getMaVe()); } catch (Exception ignored) {}
                        
                        
                        if (ve != null && "Đã đổi".equals(ve.getTrangThai())) {
                            continue;
                        }
                        
                        String maChuyen = ve != null ? ve.getMaChuyen() : "";
                        String maGhe = ve != null ? ve.getMaSoGhe() : "";
                        Float giaGoc = ct.getGiaGoc();
                        Float giaDaKM = ct.getGiaDaKM();
                        total += (giaDaKM != null ? giaDaKM : 0f);
                        m.addRow(new Object[]{
                                ct.getMaVe(),
                                ct.getMaLoaiVe(),
                                maChuyen,
                                maGhe,
                                giaGoc != null ? String.format("%.0f", giaGoc) : "",
                                giaDaKM != null ? String.format("%.0f", giaDaKM) : "",
                                ct.getMoTa() != null ? ct.getMoTa() : ""
                        });
                    }

                    JScrollPane sp = new JScrollPane(tbl);
                    dialog.add(sp, BorderLayout.CENTER);

                    
                    JPanel bottom = new JPanel(new BorderLayout(8,8));
                    JLabel lblTotal = new JLabel("Tổng tiền (đã KM): " + String.format("%.0f", total));
                    lblTotal.setFont(lblTotal.getFont().deriveFont(Font.BOLD, 14f));
                    bottom.add(lblTotal, BorderLayout.WEST);

                    JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton btnInVe = new JButton("In vé (PDF)");
                    JButton btnDong = new JButton("Đóng");
                    MaterialInitializer.styleButton(btnInVe);
                    MaterialInitializer.styleButton(btnDong);
                    actions.add(btnInVe);
                    actions.add(btnDong);
                    bottom.add(actions, BorderLayout.EAST);

                    dialog.add(bottom, BorderLayout.SOUTH);

                    // print tickets action: iterate and call veService.inVePDF
                    btnInVe.addActionListener(ae -> {
                        btnInVe.setEnabled(false);
                        SwingWorker<Void, Void> wk = new SwingWorker<>() {
                            @Override protected Void doInBackground() {
                                for (ChiTietHoaDon ct : items) {
                                    try {
                                        Ve v = veService.timVeTheoMa(ct.getMaVe());
                                        if (v != null) veService.inVePDF(v);
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                                return null;
                            }
                            @Override protected void done() {
                                btnInVe.setEnabled(true);
                                JOptionPane.showMessageDialog(dialog, "Đã gửi lệnh in vé (PDF).", "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);
                            }
                        };
                        wk.execute();
                    });

                    btnDong.addActionListener(ae -> dialog.dispose());

                    dialog.setVisible(true);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(PnlQuanLyVe.this, "Lỗi khi tải chi tiết hóa đơn: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}