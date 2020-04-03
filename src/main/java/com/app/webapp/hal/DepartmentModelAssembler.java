package com.app.webapp.hal;

import com.app.webapp.controller.rest.RestDepartmentController;
import com.app.webapp.controller.rest.RestLocationController;
import com.app.webapp.model.Department;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;


import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class DepartmentModelAssembler implements RepresentationModelAssembler<Department, Department> {
    @Override
    public Department toModel(Department department) {
        department.add(linkTo(methodOn(RestDepartmentController.class).findById(department.getId())).withSelfRel());
        department.add(linkTo(methodOn(RestDepartmentController.class).findAll()).withRel("departments"));
        department.getLocation().add(linkTo(methodOn(RestLocationController.class).findById(department.getLocation().getId())).withSelfRel());
        department.getLocation().add(linkTo(methodOn(RestLocationController.class).findAll()).withRel("locations"));

        return department;
    }

    @Override
    public CollectionModel<Department> toCollectionModel(Iterable<? extends Department> entities) {
        Collection<Department> departments = new ArrayList<>();
        entities.forEach(department -> {
            department.add(linkTo(methodOn(RestDepartmentController.class).findById(department.getId())).withSelfRel());
            if (!department.getLocation().hasLinks())
                department.getLocation().add(linkTo(methodOn(RestLocationController.class).findById(department.getLocation().getId())).withSelfRel());
            departments.add(department);
        });
        CollectionModel<Department> models = new CollectionModel<>(departments);
        models.add(linkTo(methodOn(RestDepartmentController.class).findAll()).withSelfRel());
        return models;
    }
}
