package com.app.webapp.hal;

import com.app.webapp.controller.rest.RestLocationController;
import com.app.webapp.model.Location;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class LocationModelAssembler implements RepresentationModelAssembler<Location, Location> {
    @Override
    public Location toModel(Location location) {
        location.add(linkTo(methodOn(RestLocationController.class).findById(location.getId())).withSelfRel());
        location.add(linkTo(methodOn(RestLocationController.class).findAll()).withRel("locations"));

        return location;
    }

    @Override
    public CollectionModel<Location> toCollectionModel(Iterable<? extends Location> entities) {
        Collection<Location> locations = new ArrayList<>();
        entities.forEach(location -> {
            location.add(linkTo(methodOn(RestLocationController.class).findById(location.getId())).withSelfRel());
            locations.add(location);
        });
        CollectionModel<Location> models = new CollectionModel<>(locations);
        models.add(linkTo(methodOn(RestLocationController.class).findAll()).withSelfRel());
        return models;
    }
}
