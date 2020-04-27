package com.app.bootstrapbugz.service;

import com.app.bootstrapbugz.dto.model.user.UserDto;
import com.app.bootstrapbugz.dto.request.user.ChangePasswordRequest;
import com.app.bootstrapbugz.dto.request.user.EditUserRequest;
import org.springframework.hateoas.CollectionModel;

public interface UserService {
    CollectionModel<UserDto> findAll();
    UserDto findByUsername(String username);
    UserDto edit(EditUserRequest editUserRequest);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void logoutFromAllDevices();
}
