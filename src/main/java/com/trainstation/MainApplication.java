package com.trainstation;

import com.trainstation.gui.FrmDangNhap;
import com.trainstation.util.DataInitializer;

import javax.swing.*;

// Main application
public class MainApplication {
    public static void main(String[] args) {
        // Đặt phần giao diện hệ thống
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tải dữ liệu mẫu
        DataInitializer.initializeSampleData();

        // Hiển thị form đăng nhập
        SwingUtilities.invokeLater(() -> {
            FrmDangNhap frmDangNhap = new FrmDangNhap();
            frmDangNhap.setVisible(true);
        });
    }
}