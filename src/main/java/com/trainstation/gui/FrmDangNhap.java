package com.trainstation.gui;

import com.trainstation.service.TaiKhoanService;
import com.trainstation.model.TaiKhoan;
import com.trainstation.util.UIUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Form đăng nhập (giao diện được làm lại để giống hình mẫu).
 *
 * - BackgroundPanel giữ nguyên (hỗ trợ ảnh nền).
 * - Thêm trường input bo góc, placeholder, icon tròn chồng lên input.
 * - Button bo góc lớn, chữ in đậm giữa.
 *
 * Lưu ý: để đúng màu/ảnh như mẫu, bạn có thể cung cấp file ảnh nền dark-teal,
 * hoặc thay đổi màu mặc định trong lớp bên dưới.
 */
public class FrmDangNhap extends JFrame {
    private RoundedTextField txtTenDangNhap;
    private RoundedPasswordField txtMatKhau;
    private RoundedButton btnDangNhap;
    private TaiKhoanService taiKhoanService;

    // Đường dẫn ảnh nền (nếu không có sẽ fallback màu đơn)
    private static final String BACKGROUND_IMAGE_PATH = "img/login_bg.jpg";

    public FrmDangNhap() {
        taiKhoanService = TaiKhoanService.getInstance();
        initComponents();
    }

    private void initComponents() {
        setTitle("Đăng Nhập Hệ Thống Quản Lý Ga Tàu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Make login dialog larger so scaled UI looks comfortable
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int w = Math.max(600, (int) (screen.width * 0.6));
        int h = Math.max(480, (int) (screen.height * 0.7));
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

        JPanel backgroundPanel;
        if (bgImage != null) {
            backgroundPanel = new BackgroundPanel(bgImage);
        } else {
            // fallback: plain dark-teal gradient
            backgroundPanel = new GradientPanel(new Color(2, 64, 64), new Color(5, 90, 90));
        }
        backgroundPanel.setLayout(new GridBagLayout());
        setContentPane(backgroundPanel);

        // Card panel to hold centered form (transparent)
        JPanel card = new JPanel();
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitle.setFont(new Font("Montserrat", Font.BOLD, 34));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 24, 0));
        card.add(lblTitle);

        // Determine width for inputs based on frame width
        int fieldWidth = Math.min(520, Math.max(360, getWidth() - 220));
        int fieldHeight = 56;

        // Username layered (field + left circular icon)
        JLayeredPane userLayer = new JLayeredPane();
        userLayer.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        userLayer.setMaximumSize(new Dimension(fieldWidth, fieldHeight));
        userLayer.setMinimumSize(new Dimension(fieldWidth, fieldHeight));
        userLayer.setLayout(null);

        txtTenDangNhap = new RoundedTextField(20);
        txtTenDangNhap.setPlaceholder("Tên đăng nhập");
        txtTenDangNhap.setBounds(0, 0, fieldWidth, fieldHeight);
        userLayer.add(txtTenDangNhap, Integer.valueOf(0));

        CircleIconPanel userIcon = new CircleIconPanel(CircleIconPanel.Type.USER);
        int iconSize = 48;
        // icon overlaps left (slightly inside)
        userIcon.setBounds(8, (fieldHeight - iconSize) / 2, iconSize, iconSize);
        userLayer.add(userIcon, Integer.valueOf(1));

        // add gap and then the userLayer
        card.add(userLayer);
        card.add(Box.createRigidArea(new Dimension(0, 18)));

        // Password layered (field + right circular icon)
        JLayeredPane passLayer = new JLayeredPane();
        passLayer.setPreferredSize(new Dimension(fieldWidth, fieldHeight));
        passLayer.setMaximumSize(new Dimension(fieldWidth, fieldHeight));
        passLayer.setMinimumSize(new Dimension(fieldWidth, fieldHeight));
        passLayer.setLayout(null);

        txtMatKhau = new RoundedPasswordField(20);
        txtMatKhau.setPlaceholder("Mật khẩu");
        txtMatKhau.setBounds(0, 0, fieldWidth, fieldHeight);
        passLayer.add(txtMatKhau, Integer.valueOf(0));

        CircleIconPanel lockIcon = new CircleIconPanel(CircleIconPanel.Type.LOCK);
        // icon overlays right side
        lockIcon.setBounds(fieldWidth - iconSize - 8, (fieldHeight - iconSize) / 2, iconSize, iconSize);
        passLayer.add(lockIcon, Integer.valueOf(1));

        card.add(passLayer);
        card.add(Box.createRigidArea(new Dimension(0, 28)));

        // Login button (large rounded)
        btnDangNhap = new RoundedButton("Đăng Nhập");
        btnDangNhap.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnDangNhap.setPreferredSize(new Dimension(fieldWidth, 64));
        btnDangNhap.setMaximumSize(new Dimension(fieldWidth, 64));
        btnDangNhap.setFont(new Font("Montserrat", Font.BOLD, 20));
        btnDangNhap.addActionListener(e -> xuLyDangNhap());
        card.add(btnDangNhap);

        // Enter key triggers login
        txtMatKhau.addActionListener(e -> xuLyDangNhap());

        // Add subtle spacing below
        card.add(Box.createRigidArea(new Dimension(0, 8)));

        // Place card into background centered
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        backgroundPanel.add(card, gbc);
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
     * Background panel kept from original file - draws scaled image and overlay.
     */
    private static class BackgroundPanel extends JPanel {
        private final BufferedImage image;
        private Image scaledCache;
        private int lastW = -1;
        private int lastH = -1;
        // Overlay color slightly dark
        private Color overlayColor = new Color(0, 0, 0, 100);

        public BackgroundPanel(BufferedImage image) {
            this.image = image;
            setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image == null) return;

            int w = getWidth();
            int h = getHeight();
            if (w <= 0 || h <= 0) return;

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

            g.drawImage(scaledCache, x, y, this);

            Graphics2D g2d = (Graphics2D) g.create();
            try {
                g2d.setComposite(AlphaComposite.SrcOver);
                g2d.setColor(overlayColor);
                g2d.fillRect(0, 0, w, h);
            } finally {
                g2d.dispose();
            }
        }
    }

    /**
     * Simple vertical gradient fallback background (if no image present).
     */
    private static class GradientPanel extends JPanel {
        private final Color top, bottom;

        public GradientPanel(Color top, Color bottom) {
            this.top = top;
            this.bottom = bottom;
            setLayout(new GridBagLayout());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                int w = getWidth();
                int h = getHeight();
                GradientPaint gp = new GradientPaint(0, 0, top, w, h, bottom);
                g2.setPaint(gp);
                g2.fillRect(0, 0, w, h);
            } finally {
                g2.dispose();
            }
        }
    }

    /**
     * Text field with rounded background and placeholder support.
     */
    private static class RoundedTextField extends JTextField {
        private String placeholder = "";
        private Color backgroundColor = new Color(255, 255, 255, 50);
        private Color textColor = Color.WHITE;
        private Color placeholderColor = new Color(200, 200, 200, 160);

        public RoundedTextField(int cols) {
            super(cols);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 72, 10, 16)); // padding to allow icon overlap (left)
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setForeground(textColor);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        public void setPlaceholder(String p) {
            this.placeholder = p;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int arc = 40;
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background (rounded)
                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
                // gradient subtle
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 40),
                        0, getHeight(), new Color(255, 255, 255, 20));
                g2.setPaint(gp);
                g2.fill(rr);

                // Slight darker border
                g2.setColor(new Color(255, 255, 255, 35));
                g2.draw(rr);

                // let super paint the text (with padding)
                super.paintComponent(g);

                // placeholder
                if (getText().isEmpty() && !isFocusOwner()) {
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont());
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, ins.left, textY);
                }
            } finally {
                g2.dispose();
            }
        }
    }

    private static class RoundedPasswordField extends JPasswordField {
        private String placeholder = "";
        private Color backgroundColor = new Color(255, 255, 255, 50);
        private Color textColor = Color.BLACK;
        private Color placeholderColor = new Color(200, 200, 200, 160);

        public RoundedPasswordField(int cols) {
            super(cols);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 72)); // padding to allow right icon overlap
            setFont(new Font("Segoe UI", Font.PLAIN, 16));
            setForeground(textColor);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    repaint();
                }

                @Override
                public void focusLost(FocusEvent e) {
                    repaint();
                }
            });
        }

        public void setPlaceholder(String p) {
            this.placeholder = p;
        }

        @Override
        protected void paintComponent(Graphics g) {
            int arc = 40;
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                RoundRectangle2D.Float rr = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), arc, arc);
                GradientPaint gp = new GradientPaint(0, 0, new Color(255, 255, 255, 40),
                        0, getHeight(), new Color(255, 255, 255, 20));
                g2.setPaint(gp);
                g2.fill(rr);

                g2.setColor(new Color(255, 255, 255, 35));
                g2.draw(rr);

                super.paintComponent(g);

                if (getPassword().length == 0 && !isFocusOwner()) {
                    g2.setColor(placeholderColor);
                    g2.setFont(getFont());
                    Insets ins = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    int textY = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(placeholder, ins.left, textY);
                }
            } finally {
                g2.dispose();
            }
        }
    }

    /**
     * Simple circular icon panel - draws a white circle with a simple glyph (user / lock).
     * Glyphs are vector-drawn so no external icon files required.
     */
    private static class CircleIconPanel extends JPanel {
        enum Type { USER, LOCK }

        private final Type type;
        private Color circleColor = new Color(255, 255, 255, 230);
        private Color glyphColor = new Color(2, 64, 64);

        public CircleIconPanel(Type t) {
            this.type = t;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw white circle with slight shadow
                g2.setColor(new Color(0,0,0,30));
                g2.fillOval(2, 4, w - 2, h - 2);

                g2.setColor(circleColor);
                g2.fillOval(0, 0, w - 4, h - 4);

                // draw glyph
                g2.setColor(glyphColor);
                g2.setStroke(new BasicStroke(Math.max(2f, w / 24f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                if (type == Type.USER) {
                    // head
                    int cx = (w - 4) / 2;
                    int cy = (h - 4) / 2 - 4;
                    int r = Math.max(6, (w - 4) / 6);
                    g2.drawOval(cx - r, cy - r, r * 2, r * 2);

                    // torso (arc-line)
                    int tx = cx;
                    int ty = cy + r + 2;
                    int tw = Math.max(12, (w - 4) / 2);
                    g2.drawArc(tx - tw/2, ty - 2, tw, (h/3), 0, -180);
                } else {
                    // lock: body
                    int pad = Math.max(6, w / 8);
                    int bw = w - 8 - pad;
                    int bh = h / 3;
                    int bx = (w - bw) / 2 - 2;
                    int by = (h - bh) / 2 + 4;
                    g2.drawRoundRect(bx, by, bw, bh, 6, 6);

                    // shackle
                    int sx = bx + bw/2 - bw/4;
                    int sy = by - bh/2;
                    g2.drawArc(sx, sy, bw/2, bh, 0, 180);
                }
            } finally {
                g2.dispose();
            }
        }
    }

    /**
     * Rounded button with simple pressed effect.
     */
    private static class RoundedButton extends JButton {
        private Color fillColor = Color.WHITE;
        private Color textColor = new Color(2, 64, 64);

        public RoundedButton(String text) {
            super(text);
            setOpaque(false);
            setForeground(textColor);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setUI(new BasicButtonUI());
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(200, 56));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                int arc = h;

                // background
                if (getModel().isPressed()) {
                    g2.setColor(fillColor.darker());
                } else {
                    g2.setColor(fillColor);
                }
                g2.fillRoundRect(0, 0, w, h, arc, arc);

                // subtle inner shadow
                g2.setColor(new Color(0, 0, 0, 12));
                g2.fillRoundRect(0, h - 6, w, 6, arc, arc);

                // text
                FontMetrics fm = g2.getFontMetrics(getFont());
                Rectangle r = new Rectangle(0, 0, w, h);
                int tx = r.x + (r.width - fm.stringWidth(getText())) / 2;
                int ty = r.y + (r.height - fm.getHeight()) / 2 + fm.getAscent();

                g2.setColor(getForeground());
                g2.setFont(getFont());
                g2.drawString(getText(), tx, ty);
            } finally {
                g2.dispose();
            }
        }
    }
}