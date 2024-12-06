package org.bootstrapbugz.backend.user.controller;

import java.util.List;
import org.bootstrapbugz.backend.shared.constants.Path;
import org.bootstrapbugz.backend.user.payload.dto.RoleDTO;
import org.bootstrapbugz.backend.user.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.ROLES)
public class RoleController {
  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @GetMapping
  public ResponseEntity<List<RoleDTO>> findAll() {
    return ResponseEntity.ok(roleService.findAll());
  }
}
