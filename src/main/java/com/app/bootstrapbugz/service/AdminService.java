package com.app.bootstrapbugz.service;

import com.app.bootstrapbugz.dto.request.admin.AdminRequest;
import com.app.bootstrapbugz.dto.request.admin.ChangeRoleRequest;

public interface AdminService {
    void logoutUsersFromAllDevices(AdminRequest adminRequest);

    void changeUsersRole(ChangeRoleRequest changeRoleRequest);

    void lockUsers(AdminRequest adminRequest);

    void unlockUsers(AdminRequest adminRequest);

    void activateUser(AdminRequest adminRequest);

    void deactivateUser(AdminRequest adminRequest);

    void deleteUsers(AdminRequest adminRequest);
}
