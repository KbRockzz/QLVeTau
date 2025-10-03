package com.trainstation.gui;

import com.trainstation.service.StatisticsService;
import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private StatisticsService statisticsService;
    private JLabel revenueLabel, soldLabel, refundedLabel, cancelledLabel;
    private JTextArea trainStatsArea;

    public StatisticsPanel() {
        statisticsService = StatisticsService.getInstance();
        initComponents();
        loadStatistics();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("THỐNG KÊ HOẠT ĐỘNG", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);

        // Main panel
        JPanel mainPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Tổng quan"));

        // Revenue panel
        JPanel revenuePanel = createStatPanel("💰 Tổng doanh thu", "0 VNĐ");
        revenueLabel = (JLabel) ((JPanel) revenuePanel.getComponent(1)).getComponent(0);
        summaryPanel.add(revenuePanel);

        // Sold tickets panel
        JPanel soldPanel = createStatPanel("🎫 Vé đã bán", "0");
        soldLabel = (JLabel) ((JPanel) soldPanel.getComponent(1)).getComponent(0);
        summaryPanel.add(soldPanel);

        // Refunded tickets panel
        JPanel refundedPanel = createStatPanel("↩️ Vé hoàn trả", "0");
        refundedLabel = (JLabel) ((JPanel) refundedPanel.getComponent(1)).getComponent(0);
        summaryPanel.add(refundedPanel);

        // Cancelled tickets panel
        JPanel cancelledPanel = createStatPanel("❌ Vé hủy", "0");
        cancelledLabel = (JLabel) ((JPanel) cancelledPanel.getComponent(1)).getComponent(0);
        summaryPanel.add(cancelledPanel);

        mainPanel.add(summaryPanel);

        // Train statistics panel
        JPanel trainPanel = new JPanel(new BorderLayout());
        trainPanel.setBorder(BorderFactory.createTitledBorder("Thống kê theo chuyến tàu"));
        trainStatsArea = new JTextArea();
        trainStatsArea.setEditable(false);
        trainStatsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(trainStatsArea);
        trainPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(trainPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Refresh button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton refreshButton = new JButton("🔄 Làm mới");
        refreshButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshButton.addActionListener(e -> loadStatistics());
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createStatPanel(String title, String value) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setBackground(new Color(240, 248, 255));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setBackground(new Color(240, 248, 255));
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 20));
        valuePanel.add(valueLabel);
        panel.add(valuePanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadStatistics() {
        Map<String, Object> stats = statisticsService.getAllStatistics();

        // Update summary labels
        double revenue = (double) stats.get("totalRevenue");
        revenueLabel.setText(String.format("%,.0f VNĐ", revenue));

        int sold = (int) stats.get("ticketsSold");
        soldLabel.setText(String.valueOf(sold));

        int refunded = (int) stats.get("ticketsRefunded");
        refundedLabel.setText(String.valueOf(refunded));

        int cancelled = (int) stats.get("ticketsCancelled");
        cancelledLabel.setText(String.valueOf(cancelled));

        // Update train statistics
        @SuppressWarnings("unchecked")
        Map<String, Integer> trainStats = (Map<String, Integer>) stats.get("ticketsByTrain");
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-15s | %-10s\n", "Mã chuyến tàu", "Số vé đã bán"));
        sb.append("─────────────────────────────\n");
        
        if (trainStats.isEmpty()) {
            sb.append("Chưa có dữ liệu\n");
        } else {
            for (Map.Entry<String, Integer> entry : trainStats.entrySet()) {
                sb.append(String.format("%-15s | %-10d\n", entry.getKey(), entry.getValue()));
            }
        }
        
        trainStatsArea.setText(sb.toString());
    }
}
