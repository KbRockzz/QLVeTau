package com.trainstation.gui;

import com.trainstation.dao.*;
import com.trainstation.model.*;
import com.trainstation.service.TicketService;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

public class BookTicketPanel extends JPanel {
    private TaiKhoan currentAccount;
    private TicketService ticketService;
    private TauDAO trainDAO;
    private ToaTauDAO carriageDAO;
    private GheDAO seatDAO;
    private KhachHangDAO customerDAO;
    
    private JComboBox<String> trainComboBox;
    private JComboBox<String> customerComboBox;
    private JPanel carriageListPanel;
    private JPanel seatGridPanel;
    private JLabel selectedInfoLabel;
    
    private String selectedTrainId;
    private String selectedCarriageId;
    private String selectedSeatId;
    private String selectedSeatNumber;

    public BookTicketPanel(TaiKhoan account) {
        this.currentAccount = account;
        this.ticketService = TicketService.getInstance();
        this.trainDAO = TauDAO.getInstance();
        this.carriageDAO = ToaTauDAO.getInstance();
        this.seatDAO = GheDAO.getInstance();
        this.customerDAO = KhachHangDAO.getInstance();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel - Train and customer selection
        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("Thông tin đặt vé"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        topPanel.add(new JLabel("Chọn chuyến tàu:"), gbc);
        
        gbc.gridx = 1;
        trainComboBox = new JComboBox<>();
        trainComboBox.setPreferredSize(new Dimension(300, 25));
        trainComboBox.addActionListener(e -> loadCarriages());
        topPanel.add(trainComboBox, gbc);

        gbc.gridx = 2;
        JButton refreshTrainBtn = new JButton("Làm mới");
        refreshTrainBtn.addActionListener(e -> loadTrains());
        topPanel.add(refreshTrainBtn, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        topPanel.add(new JLabel("Chọn khách hàng:"), gbc);
        
        gbc.gridx = 1;
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(new Dimension(300, 25));
        topPanel.add(customerComboBox, gbc);

        gbc.gridx = 2;
        JButton refreshCustomerBtn = new JButton("Làm mới");
        refreshCustomerBtn.addActionListener(e -> loadCustomers());
        topPanel.add(refreshCustomerBtn, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Center panel - Carriage and seat selection
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        
        // Carriage list
        JPanel carriagePanel = new JPanel(new BorderLayout());
        carriagePanel.setBorder(BorderFactory.createTitledBorder("Chọn toa tàu"));
        carriageListPanel = new JPanel();
        carriageListPanel.setLayout(new BoxLayout(carriageListPanel, BoxLayout.Y_AXIS));
        JScrollPane carriageScrollPane = new JScrollPane(carriageListPanel);
        carriageScrollPane.setPreferredSize(new Dimension(200, 400));
        carriagePanel.add(carriageScrollPane, BorderLayout.CENTER);
        
        // Seat grid
        JPanel seatPanel = new JPanel(new BorderLayout());
        seatPanel.setBorder(BorderFactory.createTitledBorder("Chọn ghế"));
        seatGridPanel = new JPanel(new GridLayout(0, 4, 10, 10));
        JScrollPane seatScrollPane = new JScrollPane(seatGridPanel);
        seatPanel.add(seatScrollPane, BorderLayout.CENTER);
        
        centerPanel.add(carriagePanel, BorderLayout.WEST);
        centerPanel.add(seatPanel, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);

        // Bottom panel - Selected info and action buttons
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        selectedInfoLabel = new JLabel("Chưa chọn ghế");
        selectedInfoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        selectedInfoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.add(selectedInfoLabel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton bookButton = new JButton("Đặt vé");
        bookButton.setPreferredSize(new Dimension(120, 35));
        bookButton.addActionListener(e -> bookTicket());
        
        JButton cancelButton = new JButton("Hủy");
        cancelButton.setPreferredSize(new Dimension(120, 35));
        cancelButton.addActionListener(e -> clearSelection());
        
        buttonPanel.add(bookButton);
        buttonPanel.add(cancelButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);

        // Load initial data
        loadTrains();
        loadCustomers();
    }

    private void loadTrains() {
        trainComboBox.removeAllItems();
        List<Tau> trains = trainDAO.findAll();
        for (Tau train : trains) {
            trainComboBox.addItem(train.getTrainId() + " - " + train.getTrainName() + 
                " (" + train.getDepartureStation() + " → " + train.getArrivalStation() + ")");
        }
    }

    private void loadCustomers() {
        customerComboBox.removeAllItems();
        List<KhachHang> customers = customerDAO.findAll();
        for (KhachHang customer : customers) {
            customerComboBox.addItem(customer.getCustomerId() + " - " + customer.getFullName());
        }
    }

    private void loadCarriages() {
        carriageListPanel.removeAll();
        seatGridPanel.removeAll();
        selectedCarriageId = null;
        selectedSeatId = null;
        
        if (trainComboBox.getSelectedItem() == null) {
            carriageListPanel.revalidate();
            carriageListPanel.repaint();
            return;
        }
        
        selectedTrainId = trainComboBox.getSelectedItem().toString().split(" - ")[0];
        List<ToaTau> carriages = carriageDAO.findByTrainId(selectedTrainId);
        
        for (ToaTau carriage : carriages) {
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
        selectedCarriageId = carriageId;
        seatGridPanel.removeAll();
        
        List<Ghe> seats = seatDAO.findByCarriageId(carriageId);
        
        for (Ghe seat : seats) {
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

    private void selectSeat(Ghe seat) {
        selectedSeatId = seat.getSeatId();
        selectedSeatNumber = seat.getSeatNumber();
        selectedInfoLabel.setText("Đã chọn: " + selectedSeatNumber + " (Toa " + selectedCarriageId + ")");
    }

    private void bookTicket() {
        if (trainComboBox.getSelectedItem() == null || 
            customerComboBox.getSelectedItem() == null ||
            selectedSeatId == null) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng chọn đầy đủ thông tin: chuyến tàu, khách hàng và ghế!", 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String customerId = customerComboBox.getSelectedItem().toString().split(" - ")[0];
            
            Ve ticket = ticketService.bookTicket(
                selectedTrainId, 
                customerId, 
                currentAccount.getEmployeeId(), 
                selectedSeatNumber
            );
            
            // Update seat status
            Ghe seat = seatDAO.findById(selectedSeatId);
            if (seat != null) {
                seat.setStatus("BOOKED");
                seatDAO.update(seat);
            }
            
            JOptionPane.showMessageDialog(this, 
                "Đặt vé thành công!\nMã vé: " + ticket.getTicketId(), 
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            
            clearSelection();
            loadSeats(selectedCarriageId);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Lỗi đặt vé: " + ex.getMessage(), 
                "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSelection() {
        selectedSeatId = null;
        selectedSeatNumber = null;
        selectedInfoLabel.setText("Chưa chọn ghế");
    }
}
