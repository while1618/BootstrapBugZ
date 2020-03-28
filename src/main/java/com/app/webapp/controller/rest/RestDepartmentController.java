package com.app.webapp.controller.rest;

import com.app.webapp.exception.DepartmentNotFoundException;
import com.app.webapp.model.Department;
import com.app.webapp.service.IDepartmentService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource messageSource;

    public RestDepartmentController(IDepartmentService departmentService, MessageSource messageSource) {
        this.departmentService = departmentService;
        this.messageSource = messageSource;
    }

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> findAll() {
        List<Department> departments = departmentService.findAll();
        if (departments.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    messageSource.getMessage("departments.notFound", null, LocaleContextHolder.getLocale()),
                    new DepartmentNotFoundException()
            );
        return ResponseEntity.ok(departments);
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<Department> findById(@PathVariable("id") Long id) {
        Department department = departmentService.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage("department.notFound", null, LocaleContextHolder.getLocale()),
                        new DepartmentNotFoundException()
                ));
        return ResponseEntity.ok(department);
    }

    //TODO: ResponseEntity.created()
    @PostMapping("/departments")
    public ResponseEntity<Department> create(@Valid @RequestBody Department department) {
        return ResponseEntity.ok(departmentService.save(department));
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> edit(@PathVariable("id") Long id, @Valid @RequestBody Department department) {
        if (!departmentService.existsById(id))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("department.wrongId", null, LocaleContextHolder.getLocale()),
                    new DepartmentNotFoundException()
            );
        department.setId(id);
        return ResponseEntity.ok(departmentService.save(department));
    }

    @DeleteMapping("/departments/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        if (!departmentService.existsById(id))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("department.wrongId", null, LocaleContextHolder.getLocale()),
                    new DepartmentNotFoundException());
        departmentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/departments")
    public ResponseEntity<?> deleteAll() {
        departmentService.deleteAll();
        return ResponseEntity.noContent().build();
    }
}
