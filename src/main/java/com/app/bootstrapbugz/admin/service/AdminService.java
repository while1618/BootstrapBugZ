package com.app.bootstrapbugz.admin.service;

import com.app.bootstrapbugz.admin.dto.request.AdminRequest;
import com.app.bootstrapbugz.admin.dto.request.ChangeRoleRequest;

public interface AdminService {
    void logoutUsersFromAllDevices(AdminRequest adminRequest);

    void changeUsersRole(ChangeRoleRequest changeRoleRequest);

    void lockUsers(AdminRequest adminRequest);

    void unlockUsers(AdminRequest adminRequest);

    void activateUser(AdminRequest adminRequest);

    void deactivateUser(AdminRequest adminRequest);

    void deleteUsers(AdminRequest adminRequest);
}
