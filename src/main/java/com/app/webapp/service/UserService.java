package com.app.webapp.service;

import com.app.webapp.dto.model.UserDto;
import com.app.webapp.dto.request.ChangePasswordRequest;
import com.app.webapp.dto.request.SignUpRequest;
import com.app.webapp.model.User;
import org.springframework.hateoas.CollectionModel;

public interface UserService {
    CollectionModel<UserDto> findAll();
    UserDto findByUsername(String username);
    void changePassword(ChangePasswordRequest changePasswordRequest);
    void logoutFromAllDevices();
}
