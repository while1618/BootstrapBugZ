package com.app.bootstrapbugz.user.service;

import com.app.bootstrapbugz.user.dto.model.UserDto;
import com.app.bootstrapbugz.user.dto.request.ChangePasswordRequest;
import com.app.bootstrapbugz.user.dto.request.EditUserRequest;
import org.springframework.hateoas.CollectionModel;

public interface UserService {
    CollectionModel<UserDto> findAll();

    UserDto findByUsername(String username);

    UserDto edit(EditUserRequest editUserRequest);

    void changePassword(ChangePasswordRequest changePasswordRequest);

    void logoutFromAllDevices();
}
