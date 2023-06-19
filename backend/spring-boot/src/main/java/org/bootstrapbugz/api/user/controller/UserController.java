package org.bootstrapbugz.api.user.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
import org.bootstrapbugz.api.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.api.user.payload.request.UsernameAvailabilityRequest;
import org.bootstrapbugz.api.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
  public ResponseEntity<List<UserDTO>> findAll() {
    return ResponseEntity.ok(userService.findAll());
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findById(@PathVariable("id") Long id) {
    return ResponseEntity.ok(userService.findById(id));
  }

  @GetMapping("/username/{username}")
  public ResponseEntity<UserDTO> findByUsername(@PathVariable("username") String username) {
    return ResponseEntity.ok(userService.findByUsername(username));
  }

  @PostMapping("/username/availability")
  public ResponseEntity<AvailabilityDTO> usernameAvailability(
      @Valid @RequestBody UsernameAvailabilityRequest usernameAvailabilityRequest) {
    return ResponseEntity.ok(userService.usernameAvailability(usernameAvailabilityRequest));
  }

  @PostMapping("/email/availability")
  public ResponseEntity<AvailabilityDTO> emailAvailability(
      @Valid @RequestBody EmailAvailabilityRequest emailAvailabilityRequest) {
    return ResponseEntity.ok(userService.emailAvailability(emailAvailabilityRequest));
  }
}
