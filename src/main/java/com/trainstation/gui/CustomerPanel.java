package com.trainstation.gui;

import com.trainstation.dao.CustomerDAO;
import com.trainstation.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPanel extends JPanel {
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private CustomerDAO customerDAO;
    private JTextField idField, nameField, phoneField, emailField, identityField, addressField;

    public CustomerPanel() {
        customerDAO = CustomerDAO.getInstance();
        initComponents();
        loadCustomers();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel at the top
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Tìm kiếm khách hàng"));
        
        JLabel searchLabel = new JLabel("Số điện thoại:");
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Tìm kiếm");
        JButton refreshButton = new JButton("Làm mới");
        
        searchButton.addActionListener(e -> searchByPhone(searchField.getText().trim()));
        refreshButton.addActionListener(e -> {
            searchField.setText("");
            loadCustomers();
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(refreshButton);
        
        add(searchPanel, BorderLayout.NORTH);

        // Table panel
        String[] columns = {"Mã KH", "Họ tên", "Số điện thoại", "Email", "CMND/CCCD", "Địa chỉ"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        customerTable = new JTable(tableModel);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomer();
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        
        // Create a panel for table with CENTER layout
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mã KH:"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        formPanel.add(idField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Họ tên:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Số điện thoại:"), gbc);
        gbc.gridx = 1;
        phoneField = new JTextField(20);
        formPanel.add(phoneField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("CMND/CCCD:"), gbc);
        gbc.gridx = 1;
        identityField = new JTextField(20);
        formPanel.add(identityField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Địa chỉ:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(20);
        formPanel.add(addressField, gbc);

        // Button panel
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Cập nhật");
        JButton deleteButton = new JButton("Xóa");
        JButton clearButton = new JButton("Làm mới");

        addButton.addActionListener(e -> addCustomer());
        updateButton.addActionListener(e -> updateCustomer());
        deleteButton.addActionListener(e -> deleteCustomer());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.findAll();
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getPhoneNumber(),
                customer.getEmail(),
                customer.getIdNumber(),
                customer.getAddress()
            });
        }
    }

    private void loadSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            emailField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            identityField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            addressField.setText(tableModel.getValueAt(selectedRow, 5).toString());
        }
    }

    private void addCustomer() {
        if (!validateForm()) return;

        String id = idField.getText().trim();
        if (customerDAO.findById(id) != null) {
            JOptionPane.showMessageDialog(this, "Mã khách hàng đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Customer customer = new Customer(
            id,
            nameField.getText().trim(),
            phoneField.getText().trim(),
            emailField.getText().trim(),
            identityField.getText().trim(),
            addressField.getText().trim()
        );

        customerDAO.add(customer);
        loadCustomers();
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm khách hàng thành công!");
    }

    private void updateCustomer() {
        if (!validateForm()) return;

        String id = idField.getText().trim();
        Customer customer = customerDAO.findById(id);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        customer.setFullName(nameField.getText().trim());
        customer.setPhoneNumber(phoneField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setIdNumber(identityField.getText().trim());
        customer.setAddress(addressField.getText().trim());

        customerDAO.update(customer);
        loadCustomers();
        clearForm();
        JOptionPane.showMessageDialog(this, "Cập nhật khách hàng thành công!");
    }

    private void deleteCustomer() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa khách hàng này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            customerDAO.delete(id);
            loadCustomers();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
        }
    }

    private boolean validateForm() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin bắt buộc!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        identityField.setText("");
        addressField.setText("");
        customerTable.clearSelection();
    }
    
    private void searchByPhone(String phoneNumber) {
        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Vui lòng nhập số điện thoại cần tìm!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.findAll();
        boolean found = false;
        
        for (Customer customer : customers) {
            if (customer.getPhoneNumber().contains(phoneNumber)) {
                tableModel.addRow(new Object[]{
                    customer.getCustomerId(),
                    customer.getFullName(),
                    customer.getPhoneNumber(),
                    customer.getEmail(),
                    customer.getIdNumber(),
                    customer.getAddress()
                });
                found = true;
            }
        }
        
        if (!found) {
            JOptionPane.showMessageDialog(this, 
                "Không tìm thấy khách hàng với số điện thoại: " + phoneNumber, 
                "Kết quả tìm kiếm", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
