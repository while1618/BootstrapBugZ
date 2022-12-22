package org.bootstrapbugz.api.user.controller;

import java.util.List;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.payload.dto.UserDto;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<List<UserDto>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{username}")
  public ResponseEntity<UserDto> findByUsername(@PathVariable("username") String username) {
    return ResponseEntity.ok(userService.findByUsername(username));
  }
}
