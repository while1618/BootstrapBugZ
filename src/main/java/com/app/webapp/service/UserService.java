package com.app.webapp.service;

import com.app.webapp.dto.model.user.UserDto;
import com.app.webapp.dto.request.user.ChangePasswordRequest;
import org.springframework.hateoas.CollectionModel;

public interface UserService {
    CollectionModel<UserDto> findAll();
    UserDto findByUsername(String username);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void logoutFromAllDevices();
}
