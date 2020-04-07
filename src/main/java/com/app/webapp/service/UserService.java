package com.app.webapp.service;

import com.app.webapp.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    User save(User user);
    void activate(User user);
    List<User> findAll();
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Optional<User> findByUsernameOrEmail(String username, String email);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}
