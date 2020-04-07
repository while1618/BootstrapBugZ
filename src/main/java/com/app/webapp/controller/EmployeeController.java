package com.app.webapp.controller;

import com.app.webapp.hal.EmployeeModelAssembler;
import com.app.webapp.error.exception.EmployeeNotFoundException;
import com.app.webapp.model.Employee;
import com.app.webapp.service.EmployeeService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EmployeeController {
    private final EmployeeService employeeService;
    private final MessageSource messageSource;
    private final EmployeeModelAssembler assembler;

    public EmployeeController(EmployeeService employeeService, MessageSource messageSource, EmployeeModelAssembler assembler) {
        this.employeeService = employeeService;
        this.messageSource = messageSource;
        this.assembler = assembler;
    }

    @GetMapping("/employees")
    public ResponseEntity<CollectionModel<Employee>> findAll() {
        List<Employee> employees = employeeService.findAll();
        if (employees.isEmpty())
            throw new EmployeeNotFoundException(messageSource.getMessage("employees.notFound", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok(assembler.toCollectionModel(employees));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> findById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException(messageSource.getMessage("employee.notFound", null, LocaleContextHolder.getLocale())));
        return ResponseEntity.ok(assembler.toModel(employee));
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        Employee model = assembler.toModel(employeeService.save(employee));
        return ResponseEntity
                .created(URI.create(model.getRequiredLink("self").getHref()))
                .body(model);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<Employee> edit(@PathVariable("id") Long id, @Valid @RequestBody Employee employee) {
        if (!employeeService.existsById(id))
            throw new EmployeeNotFoundException(messageSource.getMessage("employee.wrongId", null, LocaleContextHolder.getLocale()));
        employee.setId(id);
        return ResponseEntity.ok(assembler.toModel(employeeService.save(employee)));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (!employeeService.existsById(id))
            throw new EmployeeNotFoundException(messageSource.getMessage("employee.wrongId", null, LocaleContextHolder.getLocale()));
        employeeService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/employees")
    public ResponseEntity<?> deleteAll() {
        employeeService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
