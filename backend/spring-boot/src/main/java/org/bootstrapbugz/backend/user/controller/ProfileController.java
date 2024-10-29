package org.bootstrapbugz.backend.user.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.shared.logger.Logger;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.bootstrapbugz.backend.user.payload.request.ChangePasswordRequest;
import org.bootstrapbugz.backend.user.payload.request.PatchProfileRequest;
import org.bootstrapbugz.backend.user.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.PROFILE)
public class ProfileController {
  private final ProfileService profileService;
  private final Logger logger;

  public ProfileController(ProfileService profileService, Logger logger) {
    this.profileService = profileService;
    this.logger = logger;
  }

  @GetMapping
  public ResponseEntity<UserDTO> find() {
    logger.info("Called");
    final var response = ResponseEntity.ok(profileService.find());
    logger.info("Finished");
    return response;
  }

  @PatchMapping
  public ResponseEntity<UserDTO> patch(
      @Valid @RequestBody PatchProfileRequest patchProfileRequest) {
    logger.info("Called");
    final var response = ResponseEntity.ok(profileService.patch(patchProfileRequest));
    logger.info("Finished");
    return response;
  }

  @DeleteMapping
  public ResponseEntity<Void> delete() {
    logger.info("Called");
    profileService.delete();
    logger.info("Finished");
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/password")
  public ResponseEntity<Void> changePassword(
      @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
    logger.info("Called");
    profileService.changePassword(changePasswordRequest);
    logger.info("Finished");
    return ResponseEntity.noContent().build();
  }
}
