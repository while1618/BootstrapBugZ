package com.app.webapp.assembler;

import com.app.webapp.controller.rest.RestLocationController;
import com.app.webapp.model.Location;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LocationAssembler implements RepresentationModelAssembler<Location, EntityModel<Location>> {
    @Override
    public EntityModel<Location> toModel(Location location) {
        return new EntityModel<>(location,
                linkTo(methodOn(RestLocationController.class).findById(location.getId())).withSelfRel(),
                linkTo(methodOn(RestLocationController.class).findAll()).withRel("locations"));
    }
}
