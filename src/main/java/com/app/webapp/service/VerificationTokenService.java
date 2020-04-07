package com.app.webapp.service;

import com.app.webapp.model.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    Optional<VerificationToken> optionalFindByToken(String token);

    VerificationToken save(VerificationToken token);
    void deactivate(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
}
