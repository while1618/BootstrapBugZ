package com.app.webapp.controller.rest;

import com.app.webapp.exception.EmployeeNotFoundException;
import com.app.webapp.model.Employee;
import com.app.webapp.service.IEmployeeService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public RestEmployeeController(IEmployeeService employeeService, MessageSource messageSource) {
        this.employeeService = employeeService;
        this.messageSource = messageSource;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> findAll() {
        List<Employee> employees = employeeService.findAll();
        if (employees.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    messageSource.getMessage("employees.notFound", null, LocaleContextHolder.getLocale()),
                    new EmployeeNotFoundException()
            );
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> findById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage("employee.notFound", null, LocaleContextHolder.getLocale()),
                        new EmployeeNotFoundException()
                ));
        return ResponseEntity.ok(employee);
    }

    //TODO: ResponseEntity.created()
    @PostMapping("/employees")
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        return ResponseEntity.ok(employeeService.save(employee));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> edit(@PathVariable("id") Long id, @Valid @RequestBody Employee employee) {
        if (!employeeService.existsById(id))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("employee.wrongId", null, LocaleContextHolder.getLocale()),
                    new EmployeeNotFoundException()
            );
        employee.setId(id);
        return ResponseEntity.ok(employeeService.save(employee));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (!employeeService.existsById(id))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("employee.wrongId", null, LocaleContextHolder.getLocale()),
                    new EmployeeNotFoundException()
            );
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/employees")
    public ResponseEntity<?> deleteAll() {
        employeeService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
