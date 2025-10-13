package com.trainstation.gui;

import com.trainstation.model.TaiKhoan;
import com.trainstation.service.VeService;
import javax.swing.*;
import java.awt.*;

/**
 * Panel quản lý vé
 */
public class PnlQuanLyVe extends JPanel {
    private TaiKhoan taiKhoanHienTai;
    private VeService veService;

    public PnlQuanLyVe(TaiKhoan taiKhoan) {
        this.taiKhoanHienTai = taiKhoan;
        this.veService = VeService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel lblTieuDe = new JLabel("QUẢN LÝ VÉ", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblThongBao = new JLabel("Chức năng đang được phát triển", SwingConstants.CENTER);
        lblThongBao.setFont(new Font("Arial", Font.PLAIN, 16));

        JPanel pnlTrungTam = new JPanel();
        pnlTrungTam.setLayout(new BoxLayout(pnlTrungTam, BoxLayout.Y_AXIS));
        pnlTrungTam.add(lblTieuDe);
        pnlTrungTam.add(Box.createVerticalStrut(20));
        pnlTrungTam.add(lblThongBao);

        add(pnlTrungTam, BorderLayout.CENTER);
    }
}
