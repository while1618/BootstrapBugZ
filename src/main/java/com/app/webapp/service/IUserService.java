package com.app.webapp.service;

import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;

import java.util.Optional;

public interface IUserService {
    User save(User user);
    User getUserByToken(String token);
    boolean emailExist(String email);
    boolean usernameExist(String username);
    void createVerificationToken(User user, String token);
    Optional<VerificationToken> getVerificationToken(String token);
}
