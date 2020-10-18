package com.app.bootstrapbugz.admin;

import com.app.bootstrapbugz.admin.dto.request.AdminRequest;
import com.app.bootstrapbugz.admin.dto.request.ChangeRoleRequest;
import com.app.bootstrapbugz.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users/logout")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> logoutUserFromAllDevices(@Valid @RequestBody AdminRequest adminRequest) {
        adminService.logoutUsersFromAllDevices(adminRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changeUsersRole(@Valid @RequestBody ChangeRoleRequest changeRoleRequest) {
        adminService.changeUsersRole(changeRoleRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/lock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> lockUsers(@Valid @RequestBody AdminRequest adminRequest) {
        adminService.lockUsers(adminRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/unlock")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> unlockUsers(@Valid @RequestBody AdminRequest adminRequest) {
        adminService.unlockUsers(adminRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/activate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activateUser(@Valid @RequestBody AdminRequest adminRequest) {
        adminService.activateUser(adminRequest);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateUser(@Valid @RequestBody AdminRequest adminRequest) {
        adminService.deactivateUser(adminRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/users/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@Valid @RequestBody AdminRequest adminRequest) {
        adminService.deleteUsers(adminRequest);
        return ResponseEntity.noContent().build();
    }
}
