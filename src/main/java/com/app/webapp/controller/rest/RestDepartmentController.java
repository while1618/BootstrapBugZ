package com.app.webapp.controller.rest;

import com.app.webapp.exception.DepartmentNotFoundException;
import com.app.webapp.model.Department;
import com.app.webapp.assembler.DepartmentAssembler;
import com.app.webapp.service.IDepartmentService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RequestMapping("/api")
@RestController
public class RestDepartmentController {
    private final IDepartmentService departmentService;
    private final MessageSource messageSource;
    private final DepartmentAssembler departmentAssembler;

    public RestDepartmentController(IDepartmentService departmentService, MessageSource messageSource, DepartmentAssembler departmentAssembler) {
        this.departmentService = departmentService;
        this.messageSource = messageSource;
        this.departmentAssembler = departmentAssembler;
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
    public ResponseEntity<EntityModel<Department>> findById(@PathVariable("id") Long id) {
        Department department = departmentService.findById(id).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage("department.notFound", null, LocaleContextHolder.getLocale()),
                        new DepartmentNotFoundException()
                ));
        return ResponseEntity.ok(departmentAssembler.toModel(department));
    }

    @PostMapping("/departments")
    public ResponseEntity<EntityModel<Department>> create(@Valid @RequestBody Department department) {
        EntityModel<Department> model = departmentAssembler.toModel(departmentService.save(department));
        return ResponseEntity
                .created(URI.create(model.getRequiredLink("self").getHref()))
                .body(model);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<EntityModel<Department>> edit(@PathVariable("id") Long id, @Valid @RequestBody Department department) {
        if (!departmentService.existsById(id))
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("department.wrongId", null, LocaleContextHolder.getLocale()),
                    new DepartmentNotFoundException()
            );
        department.setId(id);
        return ResponseEntity.ok(departmentAssembler.toModel(departmentService.save(department)));
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
