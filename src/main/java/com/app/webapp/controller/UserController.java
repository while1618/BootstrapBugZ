package com.app.webapp.controller;

import com.app.webapp.hal.UserModelAssembler;
import com.app.webapp.error.exception.UserNotFoundException;
import com.app.webapp.model.User;
import com.app.webapp.service.UserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;
    private final MessageSource messageSource;
    private final UserModelAssembler assembler;

    public UserController(UserService userService, MessageSource messageSource, UserModelAssembler assembler) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.assembler = assembler;
    }

    @GetMapping("/users")
    public ResponseEntity<CollectionModel<User>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty())
            throw new UserNotFoundException(messageSource.getMessage("users.notFound", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok(assembler.toCollectionModel(users));
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable("username") String username) {
        User user = userService.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale())));
        return ResponseEntity.ok(assembler.toModel(user));
    }
}
