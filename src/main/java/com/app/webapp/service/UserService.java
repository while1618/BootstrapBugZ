package com.app.webapp.service;

import com.app.webapp.dto.model.user.UserDto;
import com.app.webapp.dto.request.user.ChangePasswordRequest;
import com.app.webapp.dto.request.user.EditUserRequest;
import org.springframework.hateoas.CollectionModel;

public interface UserService {
    CollectionModel<UserDto> findAll();
    UserDto findByUsername(String username);
    UserDto edit(EditUserRequest editUserRequest);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void logoutFromAllDevices();
}
