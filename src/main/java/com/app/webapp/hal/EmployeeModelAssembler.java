package com.app.webapp.hal;

import com.app.webapp.controller.DepartmentController;
import com.app.webapp.controller.EmployeeController;
import com.app.webapp.controller.LocationController;
import com.app.webapp.model.Employee;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmployeeModelAssembler implements RepresentationModelAssembler<Employee, Employee> {
    @Override
    public Employee toModel(Employee employee) {
        employee.add(linkTo(methodOn(EmployeeController.class).findById(employee.getId())).withSelfRel());
        employee.add(linkTo(methodOn(EmployeeController.class).findAll()).withRel("employees"));
        employee.getDepartment().add(linkTo(methodOn(DepartmentController.class).findById(employee.getDepartment().getId())).withSelfRel());
        employee.getDepartment().add(linkTo(methodOn(DepartmentController.class).findAll()).withRel("departments"));
        employee.getDepartment().getLocation().add(linkTo(methodOn(LocationController.class).findById(employee.getDepartment().getLocation().getId())).withSelfRel());
        employee.getDepartment().getLocation().add(linkTo(methodOn(LocationController.class).findAll()).withRel("locations"));

        return employee;
    }

    @Override
    public CollectionModel<Employee> toCollectionModel(Iterable<? extends Employee> entities) {
        Collection<Employee> employees = new ArrayList<>();
        entities.forEach(employee -> {
            employee.add(linkTo(methodOn(EmployeeController.class).findById(employee.getId())).withSelfRel());
            if (!employee.getDepartment().hasLinks()) {
                employee.getDepartment().add(linkTo(methodOn(DepartmentController.class).findById(employee.getDepartment().getId())).withSelfRel());
                if (!employee.getDepartment().getLocation().hasLinks())
                    employee.getDepartment().getLocation().add(linkTo(methodOn(LocationController.class).findById(employee.getDepartment().getLocation().getId())).withSelfRel());
            }
            employees.add(employee);
        });
        CollectionModel<Employee> models = new CollectionModel<>(employees);
        models.add(linkTo(methodOn(EmployeeController.class).findAll()).withSelfRel());
        return models;
    }
}
