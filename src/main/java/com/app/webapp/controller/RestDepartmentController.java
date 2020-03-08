package com.app.webapp.controller;

import com.app.webapp.model.Department;
import com.app.webapp.service.DepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RequestMapping("/api")
@RestController
public class RestDepartmentController {
    private final DepartmentService departmentService;

    public RestDepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> findAll() {
        List<Department> departments = departmentService.findAllDepartments();
        if (departments.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<Department> findById(@PathVariable("id") Long id) {
        Optional<Department> department = departmentService.findById(id);
        if (!department.isPresent())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(department.get());
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> create(@Valid @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.save(department));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> edit(@PathVariable("id") Long id, @Valid @RequestBody Department department) {
        if (!departmentService.findById(id).isPresent())
            return ResponseEntity.badRequest().build();
        department.setId(id);
        return ResponseEntity.ok(departmentService.save(department));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        if (!departmentService.findById(id).isPresent())
            return ResponseEntity.badRequest().build();
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/departments")
    public ResponseEntity<HttpStatus> deleteAll() {
        departmentService.deleteAll();
        return ResponseEntity.ok().build();
    }
}
