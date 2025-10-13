package com.trainstation.gui;

import com.trainstation.service.ThongKeService;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * Panel thống kê
 */
public class PnlThongKe extends JPanel {
    private ThongKeService thongKeService;
    private JLabel lblTongDoanhThu, lblVeDaBan, lblVeDaHoan, lblVeDaHuy;
    private JButton btnLamMoi;

    public PnlThongKe() {
        this.thongKeService = ThongKeService.getInstance();
        initComponents();
        taiDuLieuThongKe();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTieuDe = new JLabel("THỐNG KÊ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblTieuDe, BorderLayout.NORTH);

        // Statistics panel
        JPanel pnlThongKe = new JPanel(new GridLayout(2, 2, 20, 20));
        
        // Panel for each statistic
        lblTongDoanhThu = taoTheBaiThongKe("Tổng Doanh Thu", "0 VNĐ", Color.GREEN);
        lblVeDaBan = taoTheBaiThongKe("Vé Đã Bán", "0", Color.BLUE);
        lblVeDaHoan = taoTheBaiThongKe("Vé Đã Hoàn", "0", Color.ORANGE);
        lblVeDaHuy = taoTheBaiThongKe("Vé Đã Hủy", "0", Color.RED);

        pnlThongKe.add(lblTongDoanhThu);
        pnlThongKe.add(lblVeDaBan);
        pnlThongKe.add(lblVeDaHoan);
        pnlThongKe.add(lblVeDaHuy);

        add(pnlThongKe, BorderLayout.CENTER);

        // Button panel
        JPanel pnlButton = new JPanel(new FlowLayout());
        btnLamMoi = new JButton("Làm mới");
        btnLamMoi.addActionListener(e -> taiDuLieuThongKe());
        pnlButton.add(btnLamMoi);

        add(pnlButton, BorderLayout.SOUTH);
    }

    private JLabel taoTheBaiThongKe(String tieuDe, String giaTri, Color mau) {
        JLabel lbl = new JLabel("<html><div style='text-align: center;'>" +
                "<h2 style='color: " + toHex(mau) + ";'>" + tieuDe + "</h2>" +
                "<h1>" + giaTri + "</h1></div></html>", SwingConstants.CENTER);
        lbl.setBorder(BorderFactory.createLineBorder(mau, 2));
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        return lbl;
    }

    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    private void taiDuLieuThongKe() {
        Map<String, Object> thongKe = thongKeService.layTatCaThongKe();
        
        double tongDoanhThu = (double) thongKe.get("tongDoanhThu");
        int veDaBan = (int) thongKe.get("tongVeDaBan");
        int veDaHoan = (int) thongKe.get("veDaHoan");
        int veDaHuy = (int) thongKe.get("veDaHuy");

        lblTongDoanhThu.setText("<html><div style='text-align: center;'>" +
                "<h2 style='color: #00AA00;'>Tổng Doanh Thu</h2>" +
                "<h1>" + String.format("%,.0f VNĐ", tongDoanhThu) + "</h1></div></html>");
        
        lblVeDaBan.setText("<html><div style='text-align: center;'>" +
                "<h2 style='color: #0000AA;'>Vé Đã Bán</h2>" +
                "<h1>" + veDaBan + "</h1></div></html>");
        
        lblVeDaHoan.setText("<html><div style='text-align: center;'>" +
                "<h2 style='color: #FF8800;'>Vé Đã Hoàn</h2>" +
                "<h1>" + veDaHoan + "</h1></div></html>");
        
        lblVeDaHuy.setText("<html><div style='text-align: center;'>" +
                "<h2 style='color: #AA0000;'>Vé Đã Hủy</h2>" +
                "<h1>" + veDaHuy + "</h1></div></html>");
    }
}
