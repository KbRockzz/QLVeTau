package com.trainstation.dao;

import com.trainstation.model.Customer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomerDAO implements GenericDAO<Customer> {
    private static CustomerDAO instance;
    private Map<String, Customer> customers;

    private CustomerDAO() {
        customers = new HashMap<>();
    }

    public static synchronized CustomerDAO getInstance() {
        if (instance == null) {
            instance = new CustomerDAO();
        }
        return instance;
    }

    @Override
    public void add(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    @Override
    public void update(Customer customer) {
        customers.put(customer.getCustomerId(), customer);
    }

    @Override
    public void delete(String id) {
        customers.remove(id);
    }

    @Override
    public Customer findById(String id) {
        return customers.get(id);
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    public Customer findByPhoneNumber(String phoneNumber) {
        return customers.values().stream()
                .filter(c -> c.getPhoneNumber().equals(phoneNumber))
                .findFirst()
                .orElse(null);
    }
}
