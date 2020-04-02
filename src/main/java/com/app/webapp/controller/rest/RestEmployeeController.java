package com.app.webapp.controller.rest;

import com.app.webapp.assembler.EmployeeAssembler;
import com.app.webapp.error.exception.EmployeeNotFoundException;
import com.app.webapp.model.Employee;
import com.app.webapp.service.IEmployeeService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestEmployeeController {
    private final IEmployeeService employeeService;
    private final MessageSource messageSource;
    private final EmployeeAssembler employeeAssembler;

    public RestEmployeeController(IEmployeeService employeeService, MessageSource messageSource, EmployeeAssembler employeeAssembler) {
        this.employeeService = employeeService;
        this.messageSource = messageSource;
        this.employeeAssembler = employeeAssembler;
    }

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> findAll() {
        List<Employee> employees = employeeService.findAll();
        if (employees.isEmpty())
            throw new EmployeeNotFoundException(messageSource.getMessage("employees.notFound", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok(employees);
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<EntityModel<Employee>> findById(@PathVariable("id") Long id) {
        Employee employee = employeeService.findById(id).orElseThrow(
                () -> new EmployeeNotFoundException(messageSource.getMessage("employee.notFound", null, LocaleContextHolder.getLocale())));
        return ResponseEntity.ok(employeeAssembler.toModel(employee));
    }

    @PostMapping("/employees")
    public ResponseEntity<EntityModel<Employee>> create(@Valid @RequestBody Employee employee) {
        EntityModel<Employee> model = employeeAssembler.toModel(employeeService.save(employee));
        return ResponseEntity
                .created(URI.create(model.getRequiredLink("self").getHref()))
                .body(model);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EntityModel<Employee>> edit(@PathVariable("id") Long id, @Valid @RequestBody Employee employee) {
        if (!employeeService.existsById(id))
            throw new EmployeeNotFoundException(messageSource.getMessage("employee.wrongId", null, LocaleContextHolder.getLocale()));
        employee.setId(id);
        return ResponseEntity.ok(employeeAssembler.toModel(employeeService.save(employee)));
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
