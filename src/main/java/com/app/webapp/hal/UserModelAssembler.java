package com.app.webapp.hal;

import com.app.webapp.controller.rest.RestUserController;
import com.app.webapp.model.User;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserModelAssembler implements RepresentationModelAssembler<User, User> {
    @Override
    public User toModel(User user) {
        user.add(linkTo(methodOn(RestUserController.class).findByUsername(user.getUsername())).withSelfRel());
        user.add(linkTo(methodOn(RestUserController.class).findAll()).withRel("users"));

        return user;
    }

    @Override
    public CollectionModel<User> toCollectionModel(Iterable<? extends User> entities) {
        Collection<User> users = new ArrayList<>();
        entities.forEach(user -> {
            user.add(linkTo(methodOn(RestUserController.class).findByUsername(user.getUsername())).withSelfRel());
            users.add(user);
        });
        CollectionModel<User> models = new CollectionModel<>(users);
        models.add(linkTo(methodOn(RestUserController.class).findAll()).withSelfRel());
        return models;
    }
}
