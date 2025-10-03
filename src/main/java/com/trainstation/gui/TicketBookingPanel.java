package com.trainstation.gui;

import com.trainstation.dao.CustomerDAO;
import com.trainstation.dao.TrainDAO;
import com.trainstation.model.Account;
import com.trainstation.model.Customer;
import com.trainstation.model.Ticket;
import com.trainstation.model.Train;
import com.trainstation.service.TicketService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TicketBookingPanel extends JPanel {
    private JTable ticketTable;
    private DefaultTableModel tableModel;
    private TicketService ticketService;
    private CustomerDAO customerDAO;
    private TrainDAO trainDAO;
    private Account currentAccount;
    private JTextField ticketIdField, customerIdField, trainIdField, seatField;
    private JComboBox<String> trainComboBox, customerComboBox;

    public TicketBookingPanel(Account account) {
        this.currentAccount = account;
        ticketService = TicketService.getInstance();
        customerDAO = CustomerDAO.getInstance();
        trainDAO = TrainDAO.getInstance();
        initComponents();
        loadTickets();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table panel
        String[] columns = {"Mã vé", "Mã tàu", "Mã KH", "Mã NV", "Ngày đặt", "Số ghế", "Giá", "Trạng thái"};
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

        JScrollPane scrollPane = new JScrollPane(ticketTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Đặt vé"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mã vé:"), gbc);
        gbc.gridx = 1;
        ticketIdField = new JTextField(20);
        ticketIdField.setEditable(false);
        formPanel.add(ticketIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Chọn chuyến tàu:"), gbc);
        gbc.gridx = 1;
        trainComboBox = new JComboBox<>();
        updateTrainComboBox();
        formPanel.add(trainComboBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Chọn khách hàng:"), gbc);
        gbc.gridx = 1;
        customerComboBox = new JComboBox<>();
        updateCustomerComboBox();
        formPanel.add(customerComboBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Số ghế:"), gbc);
        gbc.gridx = 1;
        seatField = new JTextField(20);
        formPanel.add(seatField, gbc);

        // Button panel
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton bookButton = new JButton("Đặt vé");
        JButton refundButton = new JButton("Hoàn vé");
        JButton cancelButton = new JButton("Hủy vé");
        JButton refreshButton = new JButton("Làm mới");

        bookButton.addActionListener(e -> bookTicket());
        refundButton.addActionListener(e -> refundTicket());
        cancelButton.addActionListener(e -> cancelTicket());
        refreshButton.addActionListener(e -> {
            clearForm();
            loadTickets();
            updateTrainComboBox();
            updateCustomerComboBox();
        });

        buttonPanel.add(bookButton);
        buttonPanel.add(refundButton);
        buttonPanel.add(cancelButton);
        buttonPanel.add(refreshButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void updateTrainComboBox() {
        trainComboBox.removeAllItems();
        List<Train> trains = trainDAO.findAll();
        for (Train train : trains) {
            trainComboBox.addItem(train.getTrainId() + " - " + train.getTrainName() + 
                " (" + train.getDepartureStation() + " → " + train.getArrivalStation() + ")");
        }
    }

    private void updateCustomerComboBox() {
        customerComboBox.removeAllItems();
        List<Customer> customers = customerDAO.findAll();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer.getCustomerId() + " - " + customer.getFullName());
        }
    }

    private void loadTickets() {
        tableModel.setRowCount(0);
        List<Ticket> tickets = ticketService.getAllTickets();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Ticket ticket : tickets) {
            tableModel.addRow(new Object[]{
                ticket.getTicketId(),
                ticket.getTrainId(),
                ticket.getCustomerId(),
                ticket.getEmployeeId(),
                ticket.getBookingDate().format(formatter),
                ticket.getSeatNumber(),
                String.format("%.0f", ticket.getPrice()),
                ticket.getStatus()
            });
        }
    }

    private void loadSelectedTicket() {
        int selectedRow = ticketTable.getSelectedRow();
        if (selectedRow >= 0) {
            ticketIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
        }
    }

    private void bookTicket() {
        if (trainComboBox.getSelectedItem() == null || customerComboBox.getSelectedItem() == null ||
            seatField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String trainId = trainComboBox.getSelectedItem().toString().split(" - ")[0];
            String customerId = customerComboBox.getSelectedItem().toString().split(" - ")[0];
            String seatNumber = seatField.getText().trim();

            Ticket ticket = ticketService.bookTicket(trainId, customerId, currentAccount.getEmployeeId(), seatNumber);
            loadTickets();
            clearForm();
            updateTrainComboBox();
            JOptionPane.showMessageDialog(this, "Đặt vé thành công!\nMã vé: " + ticket.getTicketId());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi đặt vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refundTicket() {
        String ticketId = ticketIdField.getText().trim();
        if (ticketId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần hoàn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hoàn vé này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                ticketService.refundTicket(ticketId);
                loadTickets();
                clearForm();
                updateTrainComboBox();
                JOptionPane.showMessageDialog(this, "Hoàn vé thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi hoàn vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelTicket() {
        String ticketId = ticketIdField.getText().trim();
        if (ticketId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn vé cần hủy!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn hủy vé này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                ticketService.cancelTicket(ticketId);
                loadTickets();
                clearForm();
                updateTrainComboBox();
                JOptionPane.showMessageDialog(this, "Hủy vé thành công!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi hủy vé: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearForm() {
        ticketIdField.setText("");
        seatField.setText("");
        if (trainComboBox.getItemCount() > 0) {
            trainComboBox.setSelectedIndex(0);
        }
        if (customerComboBox.getItemCount() > 0) {
            customerComboBox.setSelectedIndex(0);
        }
        ticketTable.clearSelection();
    }
}
