package com.app.webapp.repository;

import com.app.webapp.model.AuthToken;
import com.app.webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByTokenAndUsage(String token, String usage);
    Optional<AuthToken> findByUserAndUsage(User user, String usage);
}
