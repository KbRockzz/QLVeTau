package com.trainstation.gui;

import com.trainstation.dao.AccountDAO;
import com.trainstation.model.Account;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AccountPanel extends JPanel {
    private JTable accountTable;
    private DefaultTableModel tableModel;
    private AccountDAO accountDAO;
    private JTextField usernameField, passwordField, employeeIdField;
    private JComboBox<String> roleComboBox;
    private JCheckBox activeCheckBox;

    public AccountPanel() {
        accountDAO = AccountDAO.getInstance();
        initComponents();
        loadAccounts();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Table panel
        String[] columns = {"Tên đăng nhập", "Vai trò", "Mã nhân viên", "Trạng thái"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedAccount();
            }
        });

        JScrollPane scrollPane = new JScrollPane(accountTable);
        add(scrollPane, BorderLayout.CENTER);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Thông tin tài khoản"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Form fields
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Tên đăng nhập:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mật khẩu:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Vai trò:"), gbc);
        gbc.gridx = 1;
        roleComboBox = new JComboBox<>(new String[]{"EMPLOYEE", "ADMIN"});
        formPanel.add(roleComboBox, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Mã nhân viên:"), gbc);
        gbc.gridx = 1;
        employeeIdField = new JTextField(20);
        formPanel.add(employeeIdField, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Trạng thái:"), gbc);
        gbc.gridx = 1;
        activeCheckBox = new JCheckBox("Kích hoạt");
        activeCheckBox.setSelected(true);
        formPanel.add(activeCheckBox, gbc);

        // Button panel
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Thêm");
        JButton updateButton = new JButton("Cập nhật");
        JButton deleteButton = new JButton("Xóa");
        JButton clearButton = new JButton("Làm mới");

        addButton.addActionListener(e -> addAccount());
        updateButton.addActionListener(e -> updateAccount());
        deleteButton.addActionListener(e -> deleteAccount());
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.SOUTH);
    }

    private void loadAccounts() {
        tableModel.setRowCount(0);
        List<Account> accounts = accountDAO.findAll();
        for (Account account : accounts) {
            tableModel.addRow(new Object[]{
                account.getUsername(),
                account.getRole(),
                account.getEmployeeId(),
                account.isIsActive() ? "Kích hoạt" : "Vô hiệu hóa"
            });
        }
    }

    private void loadSelectedAccount() {
        int selectedRow = accountTable.getSelectedRow();
        if (selectedRow >= 0) {
            String username = tableModel.getValueAt(selectedRow, 0).toString();
            Account account = accountDAO.findById(username);
            if (account != null) {
                usernameField.setText(account.getUsername());
                passwordField.setText(account.getPassword());
                roleComboBox.setSelectedItem(account.getRole());
                employeeIdField.setText(account.getEmployeeId());
                activeCheckBox.setSelected(account.isIsActive());
            }
        }
    }

    private void addAccount() {
        if (!validateForm()) return;

        String username = usernameField.getText().trim();
        if (accountDAO.findById(username) != null) {
            JOptionPane.showMessageDialog(this, "Tên đăng nhập đã tồn tại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Account account = new Account(
            username,
            passwordField.getText().trim(),
            (String) roleComboBox.getSelectedItem(),
            employeeIdField.getText().trim(),
            activeCheckBox.isSelected()
        );

        accountDAO.add(account);
        loadAccounts();
        clearForm();
        JOptionPane.showMessageDialog(this, "Thêm tài khoản thành công!");
    }

    private void updateAccount() {
        if (!validateForm()) return;

        String username = usernameField.getText().trim();
        Account account = accountDAO.findById(username);
        if (account == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy tài khoản!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        account.setPassword(passwordField.getText().trim());
        account.setRole((String) roleComboBox.getSelectedItem());
        account.setEmployeeId(employeeIdField.getText().trim());
        account.setIsActive(activeCheckBox.isSelected());

        accountDAO.update(account);
        loadAccounts();
        clearForm();
        JOptionPane.showMessageDialog(this, "Cập nhật tài khoản thành công!");
    }

    private void deleteAccount() {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn tài khoản cần xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if ("admin".equals(username)) {
            JOptionPane.showMessageDialog(this, "Không thể xóa tài khoản admin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa tài khoản này?", 
            "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            accountDAO.delete(username);
            loadAccounts();
            clearForm();
            JOptionPane.showMessageDialog(this, "Xóa tài khoản thành công!");
        }
    }

    private boolean validateForm() {
        if (usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty() ||
            employeeIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private void clearForm() {
        usernameField.setText("");
        passwordField.setText("");
        roleComboBox.setSelectedIndex(0);
        employeeIdField.setText("");
        activeCheckBox.setSelected(true);
        accountTable.clearSelection();
    }
}
