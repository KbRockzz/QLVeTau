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
     * - Bo góc 6px (từ FlatLaf theme)
     * - Màu nền #0A73D7 (xanh header)
     * - Màu chữ trắng
     * - Font Roboto Medium 14px
     * - Hover #0859A6
     * - Width động dựa trên text
     */
    public static void styleButton(JButton button) {
        if (button == null) return;
        
        // Font
        button.setFont(createFont(Font.PLAIN, 14));
        
        // Kích thước động dựa trên text, giống Navigation Bar
        String text = button.getText();
        int buttonWidth = Math.max(100, text.length() * 10 + 30);
        button.setPreferredSize(new Dimension(buttonWidth, 36));
        button.setMargin(new Insets(5, 12, 5, 12));
        
        // Cursor
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Màu nền và chữ theo yêu cầu
        button.setBackground(new Color(10, 115, 215)); // #0A73D7
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        
        // Đảm bảo nút luôn hiển thị
        button.setVisible(true);
        button.setEnabled(true);
        
        // Thêm hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            private Color originalColor = button.getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(8, 89, 166)); // #0859A6 hover
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(originalColor);
                }
            }
        });
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
    
    /**
     * Giảm kích thước bảng để form nhập liệu có đủ không gian hiển thị
     * Đặt chiều cao khoảng 45% của container height để form phía dưới hiển thị đầy đủ
     */
    public static void setTableScrollPaneSize(JScrollPane scrollPane, int heightPercentage) {
        if (scrollPane == null) return;
        
        // Lấy kích thước màn hình để tính toán
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int targetHeight = (int)(screenSize.height * heightPercentage / 100.0);
        
        // Đặt kích thước ưu tiên cho scroll pane
        scrollPane.setPreferredSize(new Dimension(0, targetHeight));
        scrollPane.setMinimumSize(new Dimension(0, Math.min(200, targetHeight)));
    }
}
