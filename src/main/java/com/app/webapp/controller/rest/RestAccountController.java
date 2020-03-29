package com.app.webapp.controller.rest;

import com.app.webapp.assembler.AccountAssembler;
import com.app.webapp.exception.UserNotFoundException;
import com.app.webapp.model.User;
import com.app.webapp.service.IUserService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RestAccountController {
    private final IUserService userService;
    private final MessageSource messageSource;
    private final AccountAssembler accountAssembler;

    public RestAccountController(IUserService userService, MessageSource messageSource, AccountAssembler accountAssembler) {
        this.userService = userService;
        this.messageSource = messageSource;
        this.accountAssembler = accountAssembler;
    }

    @GetMapping("/accounts")
    public ResponseEntity<List<User>> findAll() {
        List<User> users = userService.findAll();
        if (users.isEmpty())
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    messageSource.getMessage("users.notFound", null, LocaleContextHolder.getLocale()),
                    new UserNotFoundException()
            );
        return ResponseEntity.ok(users);
    }

    @GetMapping("/accounts/{username}")
    public ResponseEntity<EntityModel<User>> findByUsername(@PathVariable("username") String username) {
        User user = userService.findByUsername(username).orElseThrow(
                () -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()),
                        new UserNotFoundException()
                ));
        return ResponseEntity.ok(accountAssembler.toModel(user));
    }
}
