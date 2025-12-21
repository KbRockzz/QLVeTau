package com.trainstation;

import com.trainstation.config.MaterialInitializer;
import com.trainstation.gui.FrmDangNhap;
import com.trainstation.util.DataInitializer;

import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        MaterialInitializer.initUI();
        DataInitializer.initializeSampleData();

        SwingUtilities.invokeLater(() -> {
            FrmDangNhap frmDangNhap = new FrmDangNhap();
            frmDangNhap.setVisible(true);
        });
    }
}