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

            // Cấu hình font mặc định - Roboto with proper fallback
            Font defaultFont = getDefaultFont();
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

    /**
     * Tạo font mặc định với fallback logic đúng đắn
     * Ưu tiên: Roboto -> Segoe UI -> Sans Serif (system default)
     */
    private static Font getDefaultFont() {
        String[] preferredFonts = {"Roboto", "Segoe UI", "SansSerif"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        // Tìm font đầu tiên có sẵn trong danh sách ưu tiên
        for (String fontName : preferredFonts) {
            for (String availableFont : availableFonts) {
                if (availableFont.equals(fontName)) {
                    return new Font(fontName, Font.PLAIN, 14);
                }
            }
        }
        
        // Fallback to default logical font
        return new Font("SansSerif", Font.PLAIN, 14);
    }

    /**
     * Tạo font với style và size cụ thể, sử dụng fallback logic
     */
    public static Font createFont(int style, int size) {
        String[] preferredFonts = {"Roboto", "Segoe UI", "SansSerif"};
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();
        
        for (String fontName : preferredFonts) {
            for (String availableFont : availableFonts) {
                if (availableFont.equals(fontName)) {
                    return new Font(fontName, style, size);
                }
            }
        }
        
        return new Font("SansSerif", style, size);
    }

    /**
     * Áp dụng Material styling cho một nút bấm
     * - Bo góc 6px
     * - Màu nền #1976D2 (Primary Blue)
     * - Màu chữ trắng
     * - Font Roboto Medium 14px
     * - Hover #0D47A1 (Primary Dark)
     */
    public static void styleButton(JButton button) {
        if (button == null) return;
        
        // Font
        button.setFont(createFont(Font.PLAIN, 14));
        
        // Kích thước tối thiểu
        button.setPreferredSize(new Dimension(100, 36));
        
        // Cursor
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // FlatLaf sẽ tự động áp dụng màu từ theme
        // Nhưng ta có thể đảm bảo nút luôn hiển thị
        button.setVisible(true);
        button.setEnabled(true);
    }

    /**
     * Tạo và cấu hình panel chứa các nút bấm
     * Đảm bảo panel luôn hiển thị với chiều cao cố định
     */
    public static JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        panel.setPreferredSize(new Dimension(0, 60)); // Chiều cao cố định 60px
        panel.setMinimumSize(new Dimension(0, 60));
        panel.setVisible(true);
        return panel;
    }

    /**
     * Áp dụng Material styling cho tất cả nút trong một panel
     */
    public static void styleAllButtons(Container container) {
        if (container == null) return;
        
        for (Component comp : container.getComponents()) {
            if (comp instanceof JButton) {
                styleButton((JButton) comp);
            } else if (comp instanceof Container) {
                styleAllButtons((Container) comp);
            }
        }
    }
}
