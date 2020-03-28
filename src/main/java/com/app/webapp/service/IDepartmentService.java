package com.app.webapp.service;

import com.app.webapp.model.Department;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IDepartmentService {
    Page<Department> findAllDepartments(Integer page);
    void createDepartment(Department department);
    void editDepartment(Department department);
    Department findDepartmentById(Long id);



    List<Department> findAll();
    Optional<Department> findById(Long id);
    Department save(Department department);
    void deleteById(Long id);
    void deleteAll();
    boolean existsById(Long id);
}
