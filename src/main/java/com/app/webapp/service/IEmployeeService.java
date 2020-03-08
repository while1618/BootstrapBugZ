package com.app.webapp.service;

import com.app.webapp.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    Page<Employee> findAllEmployees(Integer page);
    List<Employee> findAllEmployees();
    Employee findEmployeeById(Long id);
    Optional<Employee> findById(Long id);
    Employee save(Employee employee);
    void deleteAll();
    void createEmployee(Employee employee);
    void editEmployee(Employee employee);
    void deleteEmployee(Long id);
}
