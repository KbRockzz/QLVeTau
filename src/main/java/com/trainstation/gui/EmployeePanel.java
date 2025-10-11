package com.trainstation.gui;

import com.trainstation.dao.EmployeeDAO;
import com.trainstation.model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EmployeePanel extends JPanel {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private EmployeeDAO employeeDAO;
    private JTextField idField, nameField, phoneField, emailField, positionField, hireDateField, salaryField;
    private JComboBox<String> maLoaiComboBox;

    public EmployeePanel() {
        employeeDAO = EmployeeDAO.getInstance();
        initComponents();
        loadEmployees();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table panel
        String[] columns = {"Mã NV", "Họ tên", "Số điện thoại", "Email", "Chức vụ", "Loại NV", "Ngày vào", "Lương"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(tableModel);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmployee();
            }
        });

        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mã NV:"), gbc);
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
        formPanel.add(new JLabel("Chức vụ:"), gbc);
        gbc.gridx = 1;
        positionField = new JTextField(20);
        formPanel.add(positionField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Loại nhân viên:"), gbc);
        gbc.gridx = 1;
        maLoaiComboBox = new JComboBox<>(new String[]{"LNV01", "LNV02", "LNV03"});
        formPanel.add(maLoaiComboBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Ngày vào (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        hireDateField = new JTextField(20);
        formPanel.add(hireDateField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Lương (VNĐ):"), gbc);
        gbc.gridx = 1;
        salaryField = new JTextField(20);
        formPanel.add(salaryField, gbc);

        // Button panel
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Cập nhật");
        JButton deleteButton = new JButton("Xóa");
        JButton clearButton = new JButton("Làm mới");

        addButton.addActionListener(e -> addEmployee());
        updateButton.addActionListener(e -> updateEmployee());
        deleteButton.addActionListener(e -> deleteEmployee());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.findAll();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        for (Employee employee : employees) {
            tableModel.addRow(new Object[]{
                employee.getEmployeeId(),
                employee.getFullName(),
                employee.getPhoneNumber(),
                employee.getEmail(),
                employee.getPosition(),
                employee.getMaLoai(),
                employee.getHireDate().format(formatter),
                String.format("%.0f", employee.getSalary())
            });
        }
    }

    private void loadSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            idField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            nameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            emailField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            positionField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            maLoaiComboBox.setSelectedItem(tableModel.getValueAt(selectedRow, 5).toString());
            hireDateField.setText(tableModel.getValueAt(selectedRow, 6).toString());
            salaryField.setText(tableModel.getValueAt(selectedRow, 7).toString());
        }
    }

    private void addEmployee() {
        if (!validateForm()) return;

        String id = idField.getText().trim();
        if (employeeDAO.findById(id) != null) {
            JOptionPane.showMessageDialog(this, "Mã nhân viên đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            Employee employee = new Employee(
                id,
                nameField.getText().trim(),
                phoneField.getText().trim(),
                emailField.getText().trim(),
                positionField.getText().trim(),
                (String) maLoaiComboBox.getSelectedItem(),
                LocalDate.parse(hireDateField.getText().trim(), formatter),
                Double.parseDouble(salaryField.getText().trim())
            );

            employeeDAO.add(employee);
            loadEmployees();
            clearForm();
            JOptionPane.showMessageDialog(this, "Thêm nhân viên thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEmployee() {
        if (!validateForm()) return;

        String id = idField.getText().trim();
        Employee employee = employeeDAO.findById(id);
        if (employee == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy nhân viên!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            employee.setFullName(nameField.getText().trim());
            employee.setPhoneNumber(phoneField.getText().trim());
            employee.setEmail(emailField.getText().trim());
            employee.setPosition(positionField.getText().trim());
            employee.setMaLoai((String) maLoaiComboBox.getSelectedItem());
            employee.setHireDate(LocalDate.parse(hireDateField.getText().trim(), formatter));
            employee.setSalary(Double.parseDouble(salaryField.getText().trim()));

            employeeDAO.update(employee);
            loadEmployees();
            clearForm();
            JOptionPane.showMessageDialog(this, "Cập nhật nhân viên thành công!");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Lỗi định dạng dữ liệu: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteEmployee() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa nhân viên này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            employeeDAO.delete(id);
            loadEmployees();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa nhân viên thành công!");
        }
    }

    private boolean validateForm() {
        if (idField.getText().trim().isEmpty() || nameField.getText().trim().isEmpty() ||
            phoneField.getText().trim().isEmpty() || positionField.getText().trim().isEmpty() ||
            hireDateField.getText().trim().isEmpty() || salaryField.getText().trim().isEmpty()) {
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
        positionField.setText("");
        maLoaiComboBox.setSelectedIndex(0);
        hireDateField.setText("");
        salaryField.setText("");
        employeeTable.clearSelection();
    }
}
