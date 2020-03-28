package com.app.webapp.service.registration;

import com.app.webapp.model.User;

import java.util.Optional;

public interface IUserService {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
