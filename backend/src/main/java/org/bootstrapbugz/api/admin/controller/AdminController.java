package org.bootstrapbugz.api.admin.controller;

import jakarta.validation.Valid;
import org.bootstrapbugz.api.admin.payload.request.AdminRequest;
import org.bootstrapbugz.api.admin.payload.request.UpdateRoleRequest;
import org.bootstrapbugz.api.admin.service.AdminService;
import org.bootstrapbugz.api.shared.constants.Path;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @PutMapping("/users/activate")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> activate(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.activate(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/deactivate")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> deactivate(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.deactivate(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/unlock")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> unlock(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.unlock(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/lock")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> lock(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.lock(adminRequest);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/users/update-role")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> updateRole(@Valid @RequestBody UpdateRoleRequest updateRoleRequest) {
    adminService.updateRole(updateRoleRequest);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/users/delete")
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<Void> delete(@Valid @RequestBody AdminRequest adminRequest) {
    adminService.delete(adminRequest);
    return ResponseEntity.noContent().build();
  }
}
