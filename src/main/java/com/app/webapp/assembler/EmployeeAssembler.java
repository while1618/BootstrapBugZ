package com.app.webapp.assembler;

import com.app.webapp.controller.rest.RestEmployeeController;
import com.app.webapp.model.Employee;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class EmployeeAssembler implements RepresentationModelAssembler<Employee, EntityModel<Employee>> {
    @Override
    public EntityModel<Employee> toModel(Employee employee) {
        return new EntityModel<>(employee,
                linkTo(methodOn(RestEmployeeController.class).findById(employee.getId())).withSelfRel(),
                linkTo(methodOn(RestEmployeeController.class).findAll()).withRel("employees"));
    }
}
