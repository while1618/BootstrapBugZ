package com.app.bootstrapbugz.user;

import com.app.bootstrapbugz.user.service.UserService;
import com.app.bootstrapbugz.user.dto.model.UserDto;
import com.app.bootstrapbugz.user.dto.request.ChangePasswordRequest;
import com.app.bootstrapbugz.user.dto.request.EditUserRequest;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public ResponseEntity<UserDto> edit(@Valid @RequestBody EditUserRequest editUserRequest) {
        return ResponseEntity.ok(userService.edit(editUserRequest));
    }

    @PutMapping("/users/change-password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users/logout-from-all-devices")
    public ResponseEntity<Void> logoutFromAllDevices() {
        userService.logoutFromAllDevices();
        return ResponseEntity.noContent().build();
    }
}
