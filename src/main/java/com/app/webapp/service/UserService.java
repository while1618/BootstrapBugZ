package com.app.webapp.service;

import com.app.webapp.model.User;
import com.app.webapp.model.VerificationToken;
import com.app.webapp.repository.UserRepository;
import com.app.webapp.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final VerificationTokenRepository tokenRepository;

    public UserService(UserRepository userRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserByToken(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);
        if (verificationToken.isPresent())
            return verificationToken.get().getUser();
        return null;
    }

    @Override
    public boolean emailExist(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent();
    }

    @Override
    public boolean usernameExist(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.isPresent();
    }

    @Override
    public void createVerificationToken(User user, String token) {
        tokenRepository.save(new VerificationToken(user, token));
    }

    @Override
    public Optional<VerificationToken> getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }
}
