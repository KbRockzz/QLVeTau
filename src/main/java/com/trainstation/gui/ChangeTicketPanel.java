package com.trainstation.gui;

import com.trainstation.dao.*;
import com.trainstation.model.*;
import com.trainstation.service.TicketService;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ChangeTicketPanel extends JPanel {
    private Account currentAccount;
    private TicketService ticketService;
    private TicketDAO ticketDAO;
    private TrainDAO trainDAO;
    private CarriageDAO carriageDAO;
    private SeatDAO seatDAO;
    
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private JTextField ticketIdField;
    private JComboBox<String> newTrainComboBox;
    private JPanel carriageListPanel;
    private JPanel seatGridPanel;
    private JLabel selectedInfoLabel;
    
    private String selectedTicketId;
    private String selectedNewTrainId;
    private String selectedNewCarriageId;
    private String selectedNewSeatId;
    private String selectedNewSeatNumber;

    public ChangeTicketPanel(Account account) {
        this.currentAccount = account;
        this.ticketService = TicketService.getInstance();
        this.ticketDAO = TicketDAO.getInstance();
        this.trainDAO = TrainDAO.getInstance();
        this.carriageDAO = CarriageDAO.getInstance();
        this.seatDAO = SeatDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Ticket list and selection
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
        tableScrollPane.setPreferredSize(new Dimension(600, 150));
        topPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);

        // Middle panel - Ticket and new train selection
        JPanel middlePanel = new JPanel(new GridBagLayout());
        middlePanel.setBorder(BorderFactory.createTitledBorder("Thông tin đổi vé"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        middlePanel.add(new JLabel("Mã vé cần đổi:"), gbc);
        
        gbc.gridx = 1;
        ticketIdField = new JTextField(20);
        ticketIdField.setEditable(false);
        middlePanel.add(ticketIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        middlePanel.add(new JLabel("Chọn chuyến tàu mới:"), gbc);
        
        gbc.gridx = 1;
        newTrainComboBox = new JComboBox<>();
        newTrainComboBox.setPreferredSize(new Dimension(300, 25));
        newTrainComboBox.addActionListener(e -> loadCarriages());
        middlePanel.add(newTrainComboBox, gbc);

        gbc.gridx = 2;
        JButton refreshTrainBtn = new JButton("Làm mới");
        refreshTrainBtn.addActionListener(e -> loadTrains());
        middlePanel.add(refreshTrainBtn, gbc);

        add(middlePanel, BorderLayout.CENTER);

        // Bottom panel - Carriage and seat selection
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Carriage list
        JPanel carriagePanel = new JPanel(new BorderLayout());
        carriagePanel.setBorder(BorderFactory.createTitledBorder("Chọn toa tàu mới"));
        carriageListPanel = new JPanel();
        carriageListPanel.setLayout(new BoxLayout(carriageListPanel, BoxLayout.Y_AXIS));
        JScrollPane carriageScrollPane = new JScrollPane(carriageListPanel);
        carriageScrollPane.setPreferredSize(new Dimension(200, 250));
        carriagePanel.add(carriageScrollPane, BorderLayout.CENTER);
        
        // Seat grid
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setBorder(BorderFactory.createTitledBorder("Chọn ghế mới"));
        seatGridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        JScrollPane seatScrollPane = new JScrollPane(seatGridPanel);
        seatPanel.add(seatScrollPane, BorderLayout.CENTER);
        
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.add(carriagePanel, BorderLayout.WEST);
        selectionPanel.add(seatPanel, BorderLayout.CENTER);
        
        bottomPanel.add(selectionPanel, BorderLayout.CENTER);
        
        // Action panel
        JPanel actionPanel = new JPanel(new BorderLayout(10, 10));
        
        selectedInfoLabel = new JLabel("Chưa chọn ghế mới");
        selectedInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectedInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        actionPanel.add(selectedInfoLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton changeButton = new JButton("Đổi vé");
        changeButton.setPreferredSize(new Dimension(120, 35));
        changeButton.addActionListener(e -> changeTicket());
        
        JButton refreshButton = new JButton("Làm mới");
        refreshButton.setPreferredSize(new Dimension(120, 35));
        refreshButton.addActionListener(e -> {
            loadTickets();
            clearSelection();
        });
        
        buttonPanel.add(changeButton);
        buttonPanel.add(refreshButton);
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial data
        loadTickets();
        loadTrains();
    }

    private void loadTickets() {
        tableModel.setRowCount(0);
        List<Ticket> tickets = ticketService.getAllTickets();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Ticket ticket : tickets) {
            if ("BOOKED".equals(ticket.getStatus())) {
                tableModel.addRow(new Object[]{
                    ticket.getTicketId(),
                    ticket.getTrainId(),
                    ticket.getCustomerId(),
                    ticket.getBookingDate().format(formatter),
                    ticket.getSeatNumber(),
                    ticket.getPrice().toString(),
                    "Đã đặt"
                });
            }
        }
    }

    private void loadSelectedTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow >= 0) {
            selectedTicketId = tableModel.getValueAt(selectedRow, 0).toString();
            ticketIdField.setText(selectedTicketId);
        }
    }

    private void loadTrains() {
        newTrainComboBox.removeAllItems();
        List<Train> trains = trainDAO.findAll();
        for (Train train : trains) {
            newTrainComboBox.addItem(train.getTrainId() + " - " + train.getTrainName() + 
                " (" + train.getDepartureStation() + " → " + train.getArrivalStation() + ")");
        }
    }

    private void loadCarriages() {
        carriageListPanel.removeAll();
        seatGridPanel.removeAll();
        selectedNewCarriageId = null;
        selectedNewSeatId = null;
        
        if (newTrainComboBox.getSelectedItem() == null) {
            carriageListPanel.revalidate();
            carriageListPanel.repaint();
            return;
        }
        
        selectedNewTrainId = newTrainComboBox.getSelectedItem().toString().split(" - ")[0];
        List<Carriage> carriages = carriageDAO.findByTrainId(selectedNewTrainId);
        
        for (Carriage carriage : carriages) {
            JButton carriageBtn = new JButton("Toa " + carriage.getCarriageNumber() + ": " + carriage.getCarriageName());
            carriageBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
            carriageBtn.addActionListener(e -> loadSeats(carriage.getCarriageId()));
            carriageListPanel.add(carriageBtn);
            carriageListPanel.add(Box.createVerticalStrut(5));
        }
        
        carriageListPanel.revalidate();
        carriageListPanel.repaint();
    }

    private void loadSeats(String carriageId) {
        selectedNewCarriageId = carriageId;
        seatGridPanel.removeAll();
        
        List<Seat> seats = seatDAO.findByCarriageId(carriageId);
        
        for (Seat seat : seats) {
            JButton seatBtn = new JButton(seat.getSeatNumber());
            seatBtn.setPreferredSize(new Dimension(70, 70));
            
            if ("AVAILABLE".equals(seat.getStatus())) {
                seatBtn.setBackground(new Color(46, 204, 113));
                seatBtn.setEnabled(true);
                seatBtn.addActionListener(e -> selectSeat(seat));
            } else {
                seatBtn.setBackground(new Color(231, 76, 60));
                seatBtn.setEnabled(false);
            }
            
            seatBtn.setOpaque(true);
            seatBtn.setBorderPainted(true);
            seatBtn.setBorder(new LineBorder(Color.DARK_GRAY, 2));
            seatBtn.setForeground(Color.WHITE);
            seatBtn.setFont(new Font("Arial", Font.BOLD, 12));
            
            seatGridPanel.add(seatBtn);
        }
        
        seatGridPanel.revalidate();
        seatGridPanel.repaint();
    }

    private void selectSeat(Seat seat) {
        selectedNewSeatId = seat.getSeatId();
        selectedNewSeatNumber = seat.getSeatNumber();
        selectedInfoLabel.setText("Đã chọn ghế mới: " + selectedNewSeatNumber);
    }

    private void changeTicket() {
        if (selectedTicketId == null || selectedTicketId.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn vé cần đổi!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (newTrainComboBox.getSelectedItem() == null || selectedNewSeatId == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn chuyến tàu mới và ghế mới!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, 
            "Bạn có chắc muốn đổi vé này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
            
        if (result == JOptionPane.YES_OPTION) {
            try {
                // Get old ticket to release old seat
                Ticket oldTicket = ticketDAO.findById(selectedTicketId);
                if (oldTicket != null && oldTicket.getSeatId() != null) {
                    Seat oldSeat = seatDAO.findById(oldTicket.getSeatId());
                    if (oldSeat != null) {
                        oldSeat.setStatus("AVAILABLE");
                        seatDAO.update(oldSeat);
                    }
                }
                
                // Change ticket
                ticketService.changeTicket(
                    selectedTicketId, 
                    selectedNewTrainId, 
                    selectedNewSeatNumber,
                    selectedNewSeatId,
                    selectedNewCarriageId
                );
                
                // Update new seat status
                Seat newSeat = seatDAO.findById(selectedNewSeatId);
                if (newSeat != null) {
                    newSeat.setStatus("BOOKED");
                    seatDAO.update(newSeat);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Đổi vé thành công!", 
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
                
                loadTickets();
                clearSelection();
                if (selectedNewCarriageId != null) {
                    loadSeats(selectedNewCarriageId);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Lỗi đổi vé: " + ex.getMessage(), 
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearSelection() {
        selectedTicketId = null;
        selectedNewSeatId = null;
        selectedNewSeatNumber = null;
        ticketIdField.setText("");
        selectedInfoLabel.setText("Chưa chọn ghế mới");
        ticketTable.clearSelection();
    }
}
