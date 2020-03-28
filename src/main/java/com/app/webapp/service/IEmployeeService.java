package com.app.webapp.service;

import com.app.webapp.model.Employee;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IEmployeeService {
    Page<Employee> findAllEmployees(Integer page);
    Employee findEmployeeById(Long id);
    void createEmployee(Employee employee);
    void editEmployee(Employee employee);

    List<Employee> findAll();
    Optional<Employee> findById(Long id);
    Employee save(Employee employee);
    void deleteById(Long id);
    void deleteAll();
    boolean existsById(Long id);
}
