package org.bootstrapbugz.api.admin.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.admin.service.AdminService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Path.ADMIN)
public class AdminController {
  private final AdminService adminService;

  public AdminController(AdminService adminService) {
    this.adminService = adminService;
  }

  @PutMapping("/users/{username}/activate")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> activate(@PathVariable("username") String username) {
    adminService.activate(username);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/{username}/deactivate")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> deactivate(@PathVariable("username") String username) {
    adminService.deactivate(username);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/{username}/unlock")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> unlock(@PathVariable("username") String username) {
    adminService.unlock(username);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/{username}/lock")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> lock(@PathVariable("username") String username) {
    adminService.lock(username);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/{username}/update-role")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> updateRole(
      @PathVariable("username") String username,
      @Valid @RequestBody UpdateRoleRequest updateRoleRequest) {
    adminService.updateRole(username, updateRoleRequest);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/{username}/delete")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable("username") String username) {
    adminService.delete(username);
    return ResponseEntity.noContent().build();
  }
}
