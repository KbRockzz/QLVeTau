package com.trainstation.dao;

import com.trainstation.model.Employee;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO implements GenericDAO<Employee> {
    private static EmployeeDAO instance;
    private Map<String, Employee> employees;

    private EmployeeDAO() {
        employees = new HashMap<>();
    }

    public static synchronized EmployeeDAO getInstance() {
        if (instance == null) {
            instance = new EmployeeDAO();
        }
        return instance;
    }

    @Override
    public void add(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
    }

    @Override
    public void update(Employee employee) {
        employees.put(employee.getEmployeeId(), employee);
    }

    @Override
    public void delete(String id) {
        employees.remove(id);
    }

    @Override
    public Employee findById(String id) {
        return employees.get(id);
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(employees.values());
    }

    public List<Employee> findByPosition(String position) {
        List<Employee> result = new ArrayList<>();
        for (Employee employee : employees.values()) {
            if (employee.getPosition().equals(position)) {
                result.add(employee);
            }
        }
        return result;
    }
}
