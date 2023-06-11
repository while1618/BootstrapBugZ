package org.bootstrapbugz.api.admin.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.api.admin.payload.request.PatchUserRequest;
import org.bootstrapbugz.api.admin.payload.request.UserRequest;
import org.bootstrapbugz.api.admin.service.UserService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.shared.generic.crud.CrudController;
import org.bootstrapbugz.api.user.payload.dto.UserDTO;
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

  public UserController(UserService userService) {
    super(userService);
    this.userService = userService;
  }

  @PatchMapping("/{id}")
  public ResponseEntity<UserDTO> patch(
      @PathVariable("id") Long id, @Valid @RequestBody PatchUserRequest patchUserRequest) {
    return ResponseEntity.ok(userService.patch(id, patchUserRequest));
  }
}
