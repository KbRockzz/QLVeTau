package com.trainstation.gui;

import com.trainstation.dao.*;
import com.trainstation.model.*;
import com.trainstation.service.TicketService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RefundTicketPanel extends JPanel {
    private Account currentAccount;
    private TicketService ticketService;
    private TicketDAO ticketDAO;
    private SeatDAO seatDAO;
    
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JTextField ticketIdField;
    private JTextArea ticketDetailsArea;

    public RefundTicketPanel(Account account) {
        this.currentAccount = account;
        this.ticketService = TicketService.getInstance();
        this.ticketDAO = TicketDAO.getInstance();
        this.seatDAO = SeatDAO.getInstance();
        initComponents();
        loadTickets();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Ticket list
        JPanel topPanel = new JPanel(new BorderLayout(5, 5));
        topPanel.setBorder(BorderFactory.createTitledBorder("Danh sách vé đã đặt"));
        
        String[] columns = {"Mã vé", "Mã tàu", "Khách hàng", "Ngày đặt", "Số ghế", "Giá", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        ticketTable = new JTable(tableModel);
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedTicket();
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(ticketTable);
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.CENTER);

        // Bottom panel - Ticket details and refund action
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("Thông tin hoàn vé"));
        
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        detailsPanel.add(new JLabel("Mã vé:"), gbc);
        
        gbc.gridx = 1;
        ticketIdField = new JTextField(20);
        ticketIdField.setEditable(false);
        detailsPanel.add(ticketIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        detailsPanel.add(new JLabel("Chi tiết vé:"), gbc);
        
        gbc.gridy = 2;
        ticketDetailsArea = new JTextArea(5, 40);
        ticketDetailsArea.setEditable(false);
        ticketDetailsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane detailsScrollPane = new JScrollPane(ticketDetailsArea);
        detailsPanel.add(detailsScrollPane, gbc);
        
        bottomPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refundButton = new JButton("Hoàn vé");
        refundButton.setPreferredSize(new Dimension(120, 35));
        refundButton.setBackground(new Color(231, 76, 60));
        refundButton.setForeground(Color.WHITE);
        refundButton.addActionListener(e -> refundTicket());
        
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(e -> {
            loadTickets();
            clearSelection();
        });
        
        buttonPanel.add(refundButton);
        buttonPanel.add(refreshButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadTickets() {
        tableModel.setRowCount(0);
        List<Ticket> tickets = ticketService.getAllTickets();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Ticket ticket : tickets) {
            if (ticket.getStatus() == Ticket.TicketStatus.BOOKED) {
                tableModel.addRow(new Object[]{
                    ticket.getTicketId(),
                    ticket.getTrainId(),
                    ticket.getCustomerId(),
                    ticket.getBookingDate().format(formatter),
                    ticket.getSeatNumber(),
                    String.format("%.0f", ticket.getPrice()),
                    "Đã đặt"
                });
            }
        }
    }

    private void loadSelectedTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow >= 0) {
            String ticketId = tableModel.getValueAt(selectedRow, 0).toString();
            ticketIdField.setText(ticketId);
            
            Ticket ticket = ticketDAO.findById(ticketId);
            if (ticket != null) {
                StringBuilder details = new StringBuilder();
                details.append("Mã vé: ").append(ticket.getTicketId()).append("\n");
                details.append("Mã tàu: ").append(ticket.getTrainId()).append("\n");
                details.append("Khách hàng: ").append(ticket.getCustomerId()).append("\n");
                details.append("Nhân viên: ").append(ticket.getEmployeeId()).append("\n");
                details.append("Ngày đặt: ").append(ticket.getBookingDate().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).append("\n");
                details.append("Số ghế: ").append(ticket.getSeatNumber()).append("\n");
                details.append("Giá vé: ").append(String.format("%.0f VNĐ", ticket.getPrice())).append("\n");
                details.append("Trạng thái: ").append(ticket.getStatus());
                
                ticketDetailsArea.setText(details.toString());
            }
        }
    }

    private void refundTicket() {
        String ticketId = ticketIdField.getText().trim();
        if (ticketId.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn vé cần hoàn!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn hoàn vé này?\nKhách hàng sẽ được hoàn lại tiền.", 
            "Xác nhận hoàn vé", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                // Get ticket to release seat
                Ticket ticket = ticketDAO.findById(ticketId);
                if (ticket != null && ticket.getSeatId() != null) {
                    Seat seat = seatDAO.findById(ticket.getSeatId());
                    if (seat != null) {
                        seat.setStatus(Seat.SeatStatus.AVAILABLE);
                        seatDAO.update(seat);
                    }
                }
                
                ticketService.refundTicket(ticketId);
                loadTickets();
                clearSelection();
                JOptionPane.showMessageDialog(this, 
                    "Hoàn vé thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi hoàn vé: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearSelection() {
        ticketIdField.setText("");
        ticketDetailsArea.setText("");
        ticketTable.clearSelection();
    }
}
