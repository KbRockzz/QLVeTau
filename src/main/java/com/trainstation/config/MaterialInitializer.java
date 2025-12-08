package com.trainstation.config;

import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.IntelliJTheme;
import javax.swing.*;
import java.awt.*;
import java.io.InputStream;

/**
 * MaterialInitializer - Khởi tạo giao diện Material Professional Light
 * 
 * Chức năng:
 * - Tải theme ProfessionalLight.json từ resources
 * - Cấu hình font Roboto mặc định
 * - Bật anti-aliasing cho text
 * - Áp dụng theme cho toàn bộ ứng dụng
 */
public class MaterialInitializer {

    /**
     * Khởi tạo và áp dụng Material UI theme
     * Phải được gọi trước khi tạo bất kỳ component Swing nào
     */
    public static void initUI() {
        try {
            // Tải theme từ resources
            InputStream theme = MaterialInitializer.class.getResourceAsStream("/theme/ProfessionalLight.json");
            if (theme != null) {
                IntelliJTheme.setup(theme);
            } else {
                System.err.println("Warning: ProfessionalLight.json theme not found, using default theme");
            }

            // Cấu hình font mặc định - Roboto
            // Fallback to system font if Roboto is not available
            Font defaultFont = new Font("Roboto", Font.PLAIN, 14);
            if (defaultFont.getFamily().equals("Dialog")) {
                // Roboto not available, use Segoe UI or system default
                defaultFont = new Font("Segoe UI", Font.PLAIN, 14);
            }
            UIManager.put("defaultFont", defaultFont);

            // Bật anti-aliasing cho text để hiển thị mượt mà hơn
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            // Cập nhật UI cho tất cả components
            FlatLaf.updateUI();
            
            System.out.println("Material Professional Light theme initialized successfully");
        } catch (Exception ex) {
            System.err.println("Error initializing Material theme: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
