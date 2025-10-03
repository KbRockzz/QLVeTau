package com.trainstation.gui;

import com.trainstation.dao.TrainDAO;
import com.trainstation.model.Train;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TrainPanel extends JPanel {
    private JTable trainTable;
    private DefaultTableModel tableModel;
    private TrainDAO trainDAO;
    private JTextField idField, nameField, departureField, arrivalField, seatsField, priceField;
    private JTextField departureDateField, arrivalDateField;

    public TrainPanel() {
        trainDAO = TrainDAO.getInstance();
        initComponents();
        loadTrains();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table panel
        String[] columns = {"Mã tàu", "Tên tàu", "Ga đi", "Ga đến", "Giờ đi", "Giờ đến", "Tổng chỗ", "Chỗ trống", "Giá vé"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        trainTable = new JTable(tableModel);
        trainTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        trainTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedTrain();
            }
        });

        JScrollPane scrollPane = new JScrollPane(trainTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin chuyến tàu"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mã tàu:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        formPanel.add(idField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tên tàu:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ga đi:"), gbc);
        gbc.gridx = 1;
        departureField = new JTextField(20);
        formPanel.add(departureField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ga đến:"), gbc);
        gbc.gridx = 1;
        arrivalField = new JTextField(20);
        formPanel.add(arrivalField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Giờ đi (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        departureDateField = new JTextField(20);
        formPanel.add(departureDateField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Giờ đến (yyyy-MM-dd HH:mm):"), gbc);
        gbc.gridx = 1;
        arrivalDateField = new JTextField(20);
        formPanel.add(arrivalDateField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tổng số chỗ:"), gbc);
        gbc.gridx = 1;
        seatsField = new JTextField(20);
        formPanel.add(seatsField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Giá vé (VNĐ):"), gbc);
        gbc.gridx = 1;
        priceField = new JTextField(20);
        formPanel.add(priceField, gbc);

        // Button panel
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Cập nhật");
        JButton deleteButton = new JButton("Xóa");
        JButton clearButton = new JButton("Làm mới");

        addButton.addActionListener(e -> addTrain());
        updateButton.addActionListener(e -> updateTrain());
        deleteButton.addActionListener(e -> deleteTrain());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadTrains() {
        tableModel.setRowCount(0);
        List<Train> trains = trainDAO.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (Train train : trains) {
            tableModel.addRow(new Object[]{
                train.getTrainId(),
                train.getTrainName(),
                train.getDepartureStation(),
                train.getArrivalStation(),
                train.getDepartureTime().format(formatter),
                train.getArrivalTime().format(formatter),
                train.getTotalSeats(),
                train.getAvailableSeats(),
                String.format("%.0f", train.getTicketPrice())
            });
        }
    }

    private void loadSelectedTrain() {
        int selectedRow = trainTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            departureField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            arrivalField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            departureDateField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            arrivalDateField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            seatsField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            priceField.setText(tableModel.getValueAt(selectedRow, 8).toString());
        }
    }

    private void addTrain() {
        if (!validateForm()) return;

        String id = idField.getText().trim();
        if (trainDAO.findById(id) != null) {
            JOptionPane.showMessageDialog(this, "Mã tàu đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            Train train = new Train(
                id,
                nameField.getText().trim(),
                departureField.getText().trim(),
                arrivalField.getText().trim(),
                LocalDateTime.parse(departureDateField.getText().trim(), formatter),
                LocalDateTime.parse(arrivalDateField.getText().trim(), formatter),
                Integer.parseInt(seatsField.getText().trim()),
                Double.parseDouble(priceField.getText().trim())
            );

            trainDAO.add(train);
            loadTrains();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm chuyến tàu thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTrain() {
        if (!validateForm()) return;

        String id = idField.getText().trim();
        Train train = trainDAO.findById(id);
        if (train == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy chuyến tàu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            train.setTrainName(nameField.getText().trim());
            train.setDepartureStation(departureField.getText().trim());
            train.setArrivalStation(arrivalField.getText().trim());
            train.setDepartureTime(LocalDateTime.parse(departureDateField.getText().trim(), formatter));
            train.setArrivalTime(LocalDateTime.parse(arrivalDateField.getText().trim(), formatter));
            train.setTotalSeats(Integer.parseInt(seatsField.getText().trim()));
            train.setTicketPrice(Double.parseDouble(priceField.getText().trim()));

            trainDAO.update(train);
            loadTrains();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật chuyến tàu thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTrain() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn chuyến tàu cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa chuyến tàu này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            trainDAO.delete(id);
            loadTrains();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa chuyến tàu thành công!");
        }
    }

    private boolean validateForm() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() ||
            departureField.getText().trim().isEmpty() || arrivalField.getText().trim().isEmpty() ||
            departureDateField.getText().trim().isEmpty() || arrivalDateField.getText().trim().isEmpty() ||
            seatsField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        departureField.setText("");
        arrivalField.setText("");
        departureDateField.setText("");
        arrivalDateField.setText("");
        seatsField.setText("");
        priceField.setText("");
        trainTable.clearSelection();
    }
}
