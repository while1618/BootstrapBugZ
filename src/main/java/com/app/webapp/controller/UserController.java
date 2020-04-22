package com.app.webapp.controller;

import com.app.webapp.dto.model.user.UserDto;
import com.app.webapp.dto.request.user.ChangePasswordRequest;
import com.app.webapp.dto.request.user.EditUserRequest;
import com.app.webapp.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

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

    @PutMapping("/users/edit")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> edit(@Valid @RequestBody EditUserRequest editUserRequest) {
        UserDto userDto = userService.edit(editUserRequest);
        return ResponseEntity
                .created(URI.create(userDto.getRequiredLink("self").getHref()))
                .body(userDto);
    }

    @PutMapping("/users/change-password")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/logout-from-all-devices")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> logoutFromAllDevices() {
        userService.logoutFromAllDevices();
        return ResponseEntity.noContent().build();
    }
}
