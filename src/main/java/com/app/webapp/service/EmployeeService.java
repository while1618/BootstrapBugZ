package com.app.webapp.service;

import com.app.webapp.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EmployeeService {
    Page<Employee> findAllEmployees(Integer page);
    List<Employee> findAllEmployees();
    Employee findEmployeeById(Long id);
    void createEmployee(Employee employee);
    void editEmployee(Employee employee);
    void deleteEmployee(Long id);
}
