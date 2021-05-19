package org.bootstrapbugz.api.admin.controller;

import org.bootstrapbugz.api.admin.request.AdminRequest;
import org.bootstrapbugz.api.admin.request.ChangeRoleRequest;
import org.bootstrapbugz.api.admin.service.AdminService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.bootstrapbugz.api.user.dto.UserDto;
import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.ADMIN + "/users")
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<List<UserDto>> findAllUsers() {
    return ResponseEntity.ok(adminService.findAllUsers());
  }

  @PutMapping("/role")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> changeRole(@Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
    adminService.changeRole(changeRoleRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/lock")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> lock(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.lock(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/unlock")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> unlock(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.unlock(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/activate")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> activate(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.activate(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/deactivate")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> deactivate(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.deactivate(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/delete")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> delete(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.delete(adminRequest);
    return ResponseEntity.noContent().build();
  }
}
