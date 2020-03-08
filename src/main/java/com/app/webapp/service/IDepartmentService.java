package com.app.webapp.service;

import com.app.webapp.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

public interface IDepartmentService {
    Page<Department> findAllDepartments(Integer page);
    List<Department> findAllDepartments();
    Department findDepartmentById(Long id);
    Optional<Department> findById(Long id);
    Department save(Department department);
    void deleteAll();
    void createDepartment(Department department);
    void editDepartment(Department department);
    void deleteDepartment(Long id);
}
