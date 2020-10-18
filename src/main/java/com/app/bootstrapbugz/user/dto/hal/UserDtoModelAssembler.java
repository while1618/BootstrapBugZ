package com.app.bootstrapbugz.user.dto.hal;

import com.app.bootstrapbugz.user.UserController;
import com.app.bootstrapbugz.user.dto.model.UserDto;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserDtoModelAssembler implements RepresentationModelAssembler<UserDto, UserDto> {
    @Override
    public UserDto toModel(UserDto user) {
        user.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).findByUsername(user.getUsername())).withSelfRel());
        user.add(linkTo(methodOn(UserController.class).findAll()).withRel("users"));

        return user;
    }

    @Override
    public CollectionModel<UserDto> toCollectionModel(Iterable<? extends UserDto> entities) {
        Collection<UserDto> users = new ArrayList<>();
        entities.forEach(user -> {
            user.add(linkTo(methodOn(UserController.class).findByUsername(user.getUsername())).withSelfRel());
            users.add(user);
        });
        CollectionModel<UserDto> models = CollectionModel.of(users);
        models.add(linkTo(methodOn(UserController.class).findAll()).withSelfRel());
        return models;
    }
}
