package com.trainstation;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.gui.FrmDangNhap;
import com.trainstation.util.DataInitializer;

import javax.swing.*;

// Main application
public class MainApplication {
    public static void main(String[] args) {
        // Khởi tạo Material Professional Light theme trước khi tạo UI
        MaterialInitializer.initUI();

        // Tải dữ liệu mẫu
        DataInitializer.initializeSampleData();

        // Hiển thị form đăng nhập
        SwingUtilities.invokeLater(() -> {
            FrmDangNhap frmDangNhap = new FrmDangNhap();
            frmDangNhap.setVisible(true);
        });
    }
}