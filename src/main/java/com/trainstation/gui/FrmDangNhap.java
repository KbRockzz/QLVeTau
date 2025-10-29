package com.trainstation.gui;

import com.trainstation.service.TaiKhoanService;
import com.trainstation.model.TaiKhoan;
import com.trainstation.util.UIUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Form đăng nhập (phiên bản cập nhật: hỗ trợ ảnh nền từ file hệ thống)
 */
public class FrmDangNhap extends JFrame {
    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;
    private JButton btnDangNhap;
    private TaiKhoanService taiKhoanService;

    // Đường dẫn ảnh nền (bạn cung cấp)
//    private static final String BACKGROUND_IMAGE_PATH = "C:\\THUYLEA\\HOCHANH\\ptud\\Repo\\QLVeTau\\img\\login_bg.jpg";
    private static final String BACKGROUND_IMAGE_PATH = "img/login_bg.jpg";

    public FrmDangNhap() {
        taiKhoanService = TaiKhoanService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("QLVeTau - Đăng nhập");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make login dialog larger so scaled UI looks comfortable
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max(600, (int)(screen.width * 0.45));
        int h = Math.max(380, (int)(screen.height * 0.45));
        setSize(w, h);
        setLocationRelativeTo(null);

        // Load background image (if possible)
        BufferedImage bgImage = null;
        try {
            File f = new File(BACKGROUND_IMAGE_PATH);
            if (f.exists()) {
                bgImage = ImageIO.read(f);
            } else {
                System.err.println("Background image not found: " + BACKGROUND_IMAGE_PATH);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Create background panel (custom) or fallback to plain panel
        JPanel backgroundPanel;
        if (bgImage != null) {
            backgroundPanel = new BackgroundPanel(bgImage);
        } else {
            backgroundPanel = new JPanel(new BorderLayout());
            backgroundPanel.setBackground(new Color(240, 240, 240));
        }
        backgroundPanel.setLayout(new GridBagLayout()); // center the form
        setContentPane(backgroundPanel);

        // Main form panel (card) - semi-transparent background so image visible
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setOpaque(false); // let background show through
        mainPanel.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel lblTieuDe = new JLabel("HỆ THỐNG QUẢN LÝ VÉ TÀU", SwingConstants.CENTER);
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 20));
        lblTieuDe.setForeground(Color.WHITE);
        lblTieuDe.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(lblTieuDe, gbc);

        gbc.gridwidth = 1;

        // Username
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTenDN = new JLabel("Tên đăng nhập:");
        lblTenDN.setForeground(Color.WHITE);
        mainPanel.add(lblTenDN, gbc);

        gbc.gridx = 1;
        txtTenDangNhap = new JTextField(20);
        mainPanel.add(txtTenDangNhap, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblMatKhau = new JLabel("Mật khẩu:");
        lblMatKhau.setForeground(Color.WHITE);
        mainPanel.add(lblMatKhau, gbc);

        gbc.gridx = 1;
        txtMatKhau = new JPasswordField(20);
        mainPanel.add(txtMatKhau, gbc);

        // Login button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        btnDangNhap = new JButton("Đăng nhập");
        btnDangNhap.setFont(new Font("Arial", Font.BOLD, 14));
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        mainPanel.add(btnDangNhap, gbc);

        // Add the form card to the background (centered)
        backgroundPanel.add(mainPanel, gbc);

        // Enter key to login
        txtMatKhau.addActionListener(e -> xuLyDangNhap());
    }

    private void xuLyDangNhap() {
        String tenDangNhap = txtTenDangNhap.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());

        if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập tên đăng nhập và mật khẩu!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TaiKhoan taiKhoan = taiKhoanService.xacThuc(tenDangNhap, matKhau);
        if (taiKhoan != null) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                FrmChinh frmChinh = new FrmChinh(taiKhoan);

                // Apply per-component scaling (option B) with factor 1.2
                UIUtils.scaleComponentTree(frmChinh, 1.2f);

                // Show main frame
                frmChinh.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập hoặc mật khẩu không đúng!",
                    "Lỗi đăng nhập",
                    JOptionPane.ERROR_MESSAGE);
            txtMatKhau.setText("");
        }
    }

    /**
     * Custom JPanel that paints a scaled background image (keeps aspect ratio, covers panel).
     */

    private static class BackgroundPanel extends JPanel {
        private final BufferedImage image;
        private Image scaledCache;
        private int lastW = -1;
        private int lastH = -1;

        // Overlay color: default gray-ish black with alpha (0-255). Increase alpha -> darker overlay.
        private Color overlayColor = new Color(0, 0, 0, 120); // ~47% dark overlay

        public BackgroundPanel(BufferedImage image) {
            this.image = image;
            setLayout(new GridBagLayout()); // so child (form) can be centered
        }

        /**
         * Optional: allow changing overlay color/alpha at runtime.
         */
        public void setOverlayColor(Color c) {
            this.overlayColor = c;
            // Force repaint so new color applied immediately
            scaledCache = null;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) return;

            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

            // Recreate scaled image only when size changes
            if (scaledCache == null || w != lastW || h != lastH) {
                double imgRatio = (double) image.getWidth() / image.getHeight();
                double panelRatio = (double) w / h;

                int drawW, drawH;
                if (panelRatio > imgRatio) {
                    drawW = w;
                    drawH = (int) (w / imgRatio);
                } else {
                    drawH = h;
                    drawW = (int) (h * imgRatio);
                }

                // create high-quality scaled image
                BufferedImage tmp = new BufferedImage(drawW, drawH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = tmp.createGraphics();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2.drawImage(image, 0, 0, drawW, drawH, null);
                g2.dispose();

                scaledCache = tmp;
                lastW = w;
                lastH = h;
            }

            int x = (w - scaledCache.getWidth(null)) / 2;
            int y = (h - scaledCache.getHeight(null)) / 2;

            // Draw the scaled background image
            g.drawImage(scaledCache, x, y, this);

            // Draw translucent overlay covering the whole panel (on top of image, under components)
            Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setComposite(AlphaComposite.SrcOver); // default, but explicit for clarity
                g2d.setColor(overlayColor);
                g2d.fillRect(0, 0, w, h);
            } finally {
                g2d.dispose();
            }
        }
    }
}