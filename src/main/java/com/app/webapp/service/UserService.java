package com.app.webapp.service;

import com.app.webapp.dto.model.UserDto;
import org.springframework.hateoas.CollectionModel;

public interface UserService {
    CollectionModel<UserDto> findAll();
    UserDto findByUsername(String username);
}
