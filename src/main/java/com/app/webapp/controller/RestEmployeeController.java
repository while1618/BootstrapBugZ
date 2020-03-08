package com.app.webapp.controller;

import com.app.webapp.model.Employee;
import com.app.webapp.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class RestEmployeeController {
    private final EmployeeService employeeService;

    public RestEmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> findAll() {
        List<Employee> employees = employeeService.findAllEmployees();
        if (employees.isEmpty())
            return ResponseEntity.noContent().build();
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> findById(@PathVariable("id") Long id) {
        Optional<Employee> employee = employeeService.findById(id);
        if (!employee.isPresent())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(employee.get());
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.save(employee));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> edit(@PathVariable("id") Long id, @Valid @RequestBody Employee employee) {
        if (!employeeService.findById(id).isPresent())
            return ResponseEntity.badRequest().build();
        employee.setId(id);
        return ResponseEntity.ok(employeeService.save(employee));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<HttpStatus> delete(@PathVariable("id") Long id) {
        if (!employeeService.findById(id).isPresent())
            return ResponseEntity.badRequest().build();
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/employees")
    public ResponseEntity<HttpStatus> deleteAll() {
        employeeService.deleteAll();
        return ResponseEntity.ok().build();
    }
}
