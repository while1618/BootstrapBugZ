package com.app.webapp.controller.rest;

import com.app.webapp.exception.DepartmentNotFoundException;
import com.app.webapp.model.Department;
import com.app.webapp.service.IDepartmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/api")
@RestController
public class RestDepartmentController {
    private final IDepartmentService departmentService;

    public RestDepartmentController(IDepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> findAll() {
        List<Department> departments = departmentService.findAll();
        if (departments.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No departments.", new DepartmentNotFoundException());
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<Department> findById(@PathVariable("id") Long id) {
        Department department = departmentService.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found.", new DepartmentNotFoundException()));
        return ResponseEntity.ok(department);
    }

    //TODO: ResponseEntity.created()
    @PostMapping("/departments")
    public ResponseEntity<Department> create(@Valid @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.save(department));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> edit(@PathVariable("id") Long id, @Valid @RequestBody Department department) {
        if (departmentService.existsById(id)) {
            department.setId(id);
            return ResponseEntity.ok(departmentService.save(department));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct department id.", new DepartmentNotFoundException());
        }
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (departmentService.existsById(id)) {
            departmentService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct department id.", new DepartmentNotFoundException());
        }
    }

    @DeleteMapping("/departments")
    public ResponseEntity<?> deleteAll() {
        departmentService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
