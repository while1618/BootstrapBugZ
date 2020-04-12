package com.app.webapp.controller;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public ResponseEntity<CollectionModel<UserDto>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserDto> findByUsername(@PathVariable("username") String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }
}
