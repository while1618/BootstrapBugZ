package com.app.webapp.controller.rest;

import com.app.webapp.exception.EmployeeNotFoundException;
import com.app.webapp.model.Employee;
import com.app.webapp.service.IEmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestEmployeeController {
    private final IEmployeeService employeeService;

    public RestEmployeeController(IEmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> findAll() {
        List<Employee> employees = employeeService.findAll();
        if (employees.isEmpty())
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No employees.", new EmployeeNotFoundException());
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> findById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee not found.", new EmployeeNotFoundException()));
        return ResponseEntity.ok(employee);
    }

    //TODO: ResponseEntity.created()
    @PostMapping("/employees")
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.save(employee));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> edit(@PathVariable("id") Long id, @Valid @RequestBody Employee employee) {
        if (employeeService.existsById(id)) {
            employee.setId(id);
            return ResponseEntity.ok(employeeService.save(employee));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct employee id.", new EmployeeNotFoundException());
        }
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (employeeService.existsById(id)) {
            employeeService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide correct employee id.", new EmployeeNotFoundException());
        }
    }

    @DeleteMapping("/employees")
    public ResponseEntity<?> deleteAll() {
        employeeService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
