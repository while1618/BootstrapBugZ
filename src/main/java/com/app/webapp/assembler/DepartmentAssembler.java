package com.app.webapp.assembler;

import com.app.webapp.controller.rest.RestDepartmentController;
import com.app.webapp.model.Department;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DepartmentAssembler implements RepresentationModelAssembler<Department, EntityModel<Department>> {
    @Override
    public EntityModel<Department> toModel(Department department) {
        return new EntityModel<>(department,
                linkTo(methodOn(RestDepartmentController.class).findById(department.getId())).withSelfRel(),
                linkTo(methodOn(RestDepartmentController.class).findAll()).withRel("departments"));
    }
}
