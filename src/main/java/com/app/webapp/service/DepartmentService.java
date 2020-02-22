package com.app.webapp.service;

import com.app.webapp.model.Department;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DepartmentService {
    Page<Department> findAllDepartments(Integer page);
    List<Department> findAllDepartments();
    Department findDepartmentById(Long id);
    void createDepartment(Department department);
    void editDepartment(Department department);
    void deleteDepartment(Long id);
}
