package org.bootstrapbugz.backend.admin.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.backend.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.backend.admin.payload.request.UserRequest;
import org.bootstrapbugz.backend.admin.service.UserService;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.shared.generic.crud.CrudController;
import org.bootstrapbugz.backend.shared.logger.Logger;
import org.bootstrapbugz.backend.user.payload.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("adminUserController")
@RequestMapping(Path.ADMIN_USERS)
public class UserController extends CrudController<UserDTO, UserRequest> {
  private final UserService userService;
  private final Logger logger;

  public UserController(UserService userService) {
    super(userService);
    this.userService = userService;
    this.logger = new Logger();
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDTO> patch(
      @PathVariable("id") Long id, @Valid @RequestBody PatchUserRequest patchUserRequest) {
    logger.info("Called");
    final var response = ResponseEntity.ok(userService.patch(id, patchUserRequest));
    logger.info("Finished");
    return response;
  }
}
