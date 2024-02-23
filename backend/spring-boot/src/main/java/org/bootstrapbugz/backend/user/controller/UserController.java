package org.bootstrapbugz.backend.user.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.backend.shared.payload.dto.PageableDTO;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.backend.user.payload.request.UsernameAvailabilityRequest;
import org.bootstrapbugz.backend.user.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.USERS)
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<PageableDTO<UserDTO>> findAll(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit) {
    return ResponseEntity.ok(userService.findAll(PageRequest.of(page, limit, Sort.by("id"))));
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
