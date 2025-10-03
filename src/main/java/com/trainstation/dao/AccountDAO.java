package com.trainstation.dao;

import com.trainstation.model.Account;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountDAO implements GenericDAO<Account> {
    private static AccountDAO instance;
    private Map<String, Account> accounts;

    private AccountDAO() {
        accounts = new HashMap<>();
        // Initialize with default admin account
        Account admin = new Account("admin", "admin123", "ADMIN", "EMP001", true);
        accounts.put(admin.getUsername(), admin);
    }

    public static synchronized AccountDAO getInstance() {
        if (instance == null) {
            instance = new AccountDAO();
        }
        return instance;
    }

    @Override
    public void add(Account account) {
        accounts.put(account.getUsername(), account);
    }

    @Override
    public void update(Account account) {
        accounts.put(account.getUsername(), account);
    }

    @Override
    public void delete(String username) {
        accounts.remove(username);
    }

    @Override
    public Account findById(String username) {
        return accounts.get(username);
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts.values());
    }

    public Account authenticate(String username, String password) {
        Account account = accounts.get(username);
        if (account != null && account.getPassword().equals(password) && account.isActive()) {
            return account;
        }
        return null;
    }

    public List<Account> findByRole(String role) {
        List<Account> result = new ArrayList<>();
        for (Account account : accounts.values()) {
            if (account.getRole().equals(role)) {
                result.add(account);
            }
        }
        return result;
    }
}
