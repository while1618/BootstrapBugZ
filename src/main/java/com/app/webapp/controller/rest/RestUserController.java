package com.app.webapp.controller.rest;

import com.app.webapp.assembler.UserAssembler;
import com.app.webapp.error.exception.UserNotFoundException;
import com.app.webapp.model.User;
import com.app.webapp.service.IUserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestUserController {
    private final IUserService userService;
    private final MessageSource messageSource;
    private final UserAssembler userAssembler;

    public RestUserController(IUserService userService, MessageSource messageSource, UserAssembler userAssembler) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.userAssembler = userAssembler;
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty())
            throw new UserNotFoundException(messageSource.getMessage("users.notFound", null, LocaleContextHolder.getLocale()));
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<EntityModel<User>> findByUsername(@PathVariable("username") String username) {
        User user = userService.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale())));
        return ResponseEntity.ok(userAssembler.toModel(user));
    }
}
