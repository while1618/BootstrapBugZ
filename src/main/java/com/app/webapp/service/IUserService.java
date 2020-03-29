package com.app.webapp.service;

import com.app.webapp.model.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {
    User save(User user);
    List<User> findAll();
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
