package com.app.webapp.service;

import com.app.webapp.model.User;

public interface IUserService {
    User save(User user);
    boolean emailExist(String email);
    boolean usernameExist(String username);
}
