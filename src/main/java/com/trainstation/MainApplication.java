package com.trainstation;

import com.trainstation.gui.FrmDangNhap;
import com.trainstation.util.DataInitializer;
import javax.swing.*;

public class MainApplication {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Initialize sample data
        DataInitializer.initializeSampleData();

        // Start the application on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            FrmDangNhap frmDangNhap = new FrmDangNhap();
            frmDangNhap.setVisible(true);
        });
    }
}
