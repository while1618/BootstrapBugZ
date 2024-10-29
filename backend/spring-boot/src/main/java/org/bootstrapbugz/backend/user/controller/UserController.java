package org.bootstrapbugz.backend.user.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.shared.logger.Logger;
import org.bootstrapbugz.backend.shared.payload.dto.AvailabilityDTO;
import org.bootstrapbugz.backend.shared.payload.dto.PageableDTO;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.EmailAvailabilityRequest;
import org.bootstrapbugz.backend.user.payload.request.UsernameAvailabilityRequest;
import org.bootstrapbugz.backend.user.service.UserService;
import org.springframework.data.domain.Pageable;
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
  private final Logger logger;

  public UserController(UserService userService, Logger logger) {
    this.userService = userService;
    this.logger = logger;
  }

  @GetMapping
  public ResponseEntity<PageableDTO<UserDTO>> findAll(Pageable pageable) {
    logger.info("Called");
    final var response = ResponseEntity.ok(userService.findAll(pageable));
    logger.info("Finished");
    return response;
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findById(@PathVariable("id") Long id) {
    logger.info("Called");
    final var response = ResponseEntity.ok(userService.findById(id));
    logger.info("Finished");
    return response;
  }

  @GetMapping("/username/{username}")
  public ResponseEntity<UserDTO> findByUsername(@PathVariable("username") String username) {
    logger.info("Called");
    final var response = ResponseEntity.ok(userService.findByUsername(username));
    logger.info("Finished");
    return response;
  }

  @PostMapping("/username/availability")
  public ResponseEntity<AvailabilityDTO> usernameAvailability(
      @Valid @RequestBody UsernameAvailabilityRequest usernameAvailabilityRequest) {
    logger.info("Called");
    final var response =
        ResponseEntity.ok(userService.usernameAvailability(usernameAvailabilityRequest));
    logger.info("Finished");
    return response;
  }

  @PostMapping("/email/availability")
  public ResponseEntity<AvailabilityDTO> emailAvailability(
      @Valid @RequestBody EmailAvailabilityRequest emailAvailabilityRequest) {
    logger.info("Called");
    final var response = ResponseEntity.ok(userService.emailAvailability(emailAvailabilityRequest));
    logger.info("Finished");
    return response;
  }
}
