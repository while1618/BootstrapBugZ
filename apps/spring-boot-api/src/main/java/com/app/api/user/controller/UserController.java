package com.app.api.user.controller;

import com.app.api.shared.constants.Path;
import com.app.api.user.dto.SimpleUserDto;
import com.app.api.user.request.ChangePasswordRequest;
import com.app.api.user.request.UpdateUserRequest;
import com.app.api.user.service.UserService;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.USERS)
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<SimpleUserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{username}")
  public ResponseEntity<SimpleUserDto> findByUsername(@PathVariable("username") String username) {
    return ResponseEntity.ok(userService.findByUsername(username));
  }

  @PutMapping("/update")
  public ResponseEntity<SimpleUserDto> update(
      @Valid @RequestBody UpdateUserRequest updateUserRequest) {
    return ResponseEntity.ok(userService.update(updateUserRequest));
  }

  @PutMapping("/change-password")
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    userService.changePassword(changePasswordRequest);
    return ResponseEntity.noContent().build();
  }
}
