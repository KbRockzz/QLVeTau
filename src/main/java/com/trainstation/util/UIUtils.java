package com.trainstation.util;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Utility helpers for scaling Swing component tree at runtime.
 * - scaleComponentTree(component, scale): recursively scales fonts and some component properties.
 * - adjustTableForScale(table, scale): tweaks JTable row height, header font and renderers.
 *
 * Important:
 * - Call scaleComponentTree(...) after the top-level window and ALL child panels/tables are created.
 * - If tables are created or re-populated later, call adjustTableForScale(table, scale) for those tables.
 */
public final class UIUtils {
    private static final String CLIENT_PROP_SCALED = "ui.scaled";

    private UIUtils() {}

    public static void scaleComponentTree(Component comp, float scale) {
        if (comp == null || scale <= 0f || Math.abs(scale - 1.0f) < 1e-6) return;
        // If root has already been scaled, skip to avoid double-scaling
        if (comp instanceof JRootPane) {
            Object flag = ((JRootPane) comp).getClientProperty(CLIENT_PROP_SCALED);
            if (Boolean.TRUE.equals(flag)) return;
        } else if (comp instanceof RootPaneContainer) {
            RootPaneContainer rpc = (RootPaneContainer) comp;
            JRootPane rp = rpc.getRootPane();
            if (rp != null) {
                Object flag = rp.getClientProperty(CLIENT_PROP_SCALED);
                if (Boolean.TRUE.equals(flag)) return;
            }
        }

        scaleComponentTreeInternal(comp, scale);

        // Adjust all tables after fonts changed
        if (comp instanceof Container) adjustAllTables((Container) comp, scale);

        // Mark root as scaled to avoid double-scaling later
        if (comp instanceof RootPaneContainer) {
            RootPaneContainer rpc = (RootPaneContainer) comp;
            if (rpc.getRootPane() != null) rpc.getRootPane().putClientProperty(CLIENT_PROP_SCALED, Boolean.TRUE);
        } else if (comp instanceof JRootPane) {
            ((JRootPane) comp).putClientProperty(CLIENT_PROP_SCALED, Boolean.TRUE);
        }

        // Revalidate/repaint
        SwingUtilities.invokeLater(() -> {
            comp.revalidate();
            comp.repaint();
            // Update UI tree for all windows (safe)
            for (Window win : Window.getWindows()) {
                SwingUtilities.updateComponentTreeUI(win);
            }
        });
    }

    private static void scaleComponentTreeInternal(Component comp, float scale) {
        if (comp == null) return;

        // If component already scaled (per-component), skip to avoid double-scaling
        if (comp instanceof JComponent) {
            Object scaled = ((JComponent) comp).getClientProperty(CLIENT_PROP_SCALED);
            if (Boolean.TRUE.equals(scaled)) {
                // still recurse into children because some children might not be scaled
            }
        }

        // Scale font
        Font f = comp.getFont();
        if (f != null) {
            try {
                Font newFont = f.deriveFont(f.getSize2D() * scale);
                comp.setFont(newFont);
            } catch (Exception ignored) {
            }
        }

        // Buttons: increase margins & preferred size a bit
        if (comp instanceof AbstractButton) {
            AbstractButton b = (AbstractButton) comp;
            Insets in = b.getMargin();
            if (in == null) in = new Insets(4, 8, 4, 8);
            Insets newIn = new Insets(Math.max(4, Math.round(in.top * scale)),
                    Math.max(6, Math.round(in.left * scale)),
                    Math.max(4, Math.round(in.bottom * scale)),
                    Math.max(6, Math.round(in.right * scale)));
            b.setMargin(newIn);
            Dimension ps = b.getPreferredSize();
            if (ps != null) {
                b.setPreferredSize(new Dimension(Math.max(ps.width, Math.round(ps.width * scale)),
                        Math.max(ps.height, Math.round(ps.height * scale))));
            }
        }

        // Recurse children
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                scaleComponentTreeInternal(child, scale);
            }
        }

        // mark this component as scaled
        if (comp instanceof JComponent) {
            ((JComponent) comp).putClientProperty(CLIENT_PROP_SCALED, Boolean.TRUE);
        }
    }

    // Recursively find and adjust all tables under container
    public static void adjustAllTables(Container root, float scale) {
        for (Component c : root.getComponents()) {
            if (c instanceof JTable) {
                adjustTableForScale((JTable) c, scale);
            } else if (c instanceof JScrollPane) {
                JScrollPane sp = (JScrollPane) c;
                Component view = sp.getViewport().getView();
                if (view instanceof JTable) adjustTableForScale((JTable) view, scale);
                else if (view instanceof Container) adjustAllTables((Container) view, scale);
            } else if (c instanceof Container) {
                adjustAllTables((Container) c, scale);
            }
        }
    }

    public static void adjustTableForScale(JTable table, float scale) {
        if (table == null) return;

        // Avoid double-adjust
        Object flag = table.getClientProperty(CLIENT_PROP_SCALED);
        if (Boolean.TRUE.equals(flag)) return;

        // Set table font (if present)
        Font tf = table.getFont();
        if (tf != null) table.setFont(tf.deriveFont(tf.getSize2D() * scale));

        // Row height: base on font metrics
        FontMetrics fm = table.getFontMetrics(table.getFont());
        int computedRow = Math.max(24, fm.getHeight() + 6); // padding
        table.setRowHeight(computedRow);

        // Header
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            Font hf = header.getFont();
            if (hf != null) header.setFont(hf.deriveFont(hf.getSize2D() * scale));
            int headerHeight = Math.max(28, header.getFontMetrics(header.getFont()).getHeight() + 8);
            Dimension ph = header.getPreferredSize();
            header.setPreferredSize(new Dimension(ph.width, headerHeight));
        }

        // Update default renderers so they use updated font
        updateTableRenderersFont(table, scale);

        // Update editors
        TableColumnModel colModel = table.getColumnModel();
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            TableColumn col = colModel.getColumn(i);
            TableCellEditor editor = col.getCellEditor();
            if (editor instanceof DefaultCellEditor) {
                Component edComp = ((DefaultCellEditor) editor).getComponent();
                if (edComp != null && edComp.getFont() != null) {
                    edComp.setFont(edComp.getFont().deriveFont(edComp.getFont().getSize2D() * scale));
                }
            }
        }

        // Adjust column widths best-effort
        for (int i = 0; i < colModel.getColumnCount(); i++) {
            TableColumn col = colModel.getColumn(i);
            int pref = col.getPreferredWidth();
            if (pref <= 0) pref = 100;
            col.setPreferredWidth(Math.max(60, Math.round(pref * scale)));
        }

        // Intercell spacing
        Dimension spacing = table.getIntercellSpacing();
        table.setIntercellSpacing(new Dimension(Math.max(4, Math.round(spacing.width * scale)), Math.max(2, Math.round(spacing.height * scale))));
        table.setRowMargin(Math.max(2, Math.round(table.getRowMargin() * scale)));

        // mark as scaled
        table.putClientProperty(CLIENT_PROP_SCALED, Boolean.TRUE);

        // Force update
        SwingUtilities.invokeLater(() -> {
            table.revalidate();
            table.repaint();
            if (table.getParent() != null) table.getParent().revalidate();
        });
    }

    private static void updateTableRenderersFont(JTable table, float scale) {
        Font base = table.getFont();
        if (base == null) base = UIManager.getFont("Table.font");
        Font scaled = (base != null) ? base.deriveFont(base.getSize2D() * scale) : null;

        TableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (scaled != null) c.setFont(scaled);
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, defaultRenderer);
        table.setDefaultRenderer(String.class, defaultRenderer);
        table.setDefaultRenderer(Number.class, defaultRenderer);
        table.setDefaultRenderer(Boolean.class, defaultRenderer);
        table.setDefaultRenderer(java.util.Date.class, defaultRenderer);

        // Header renderer
        JTableHeader header = table.getTableHeader();
        if (header != null) {
            TableCellRenderer hr = header.getDefaultRenderer();
            TableCellRenderer newHeaderRenderer = (tbl, value, isSelected, hasFocus, row, col) -> {
                Component c;
                if (hr != null) c = hr.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                else c = new DefaultTableCellRenderer().getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
                if (scaled != null) c.setFont(scaled.deriveFont(scaled.getSize2D() * 1.0f));
                if (c instanceof JComponent) ((JComponent) c).setBorder(UIManager.getBorder("TableHeader.cellBorder"));
                return c;
            };
            header.setDefaultRenderer(newHeaderRenderer);
        }
    }
}