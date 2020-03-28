package com.app.webapp.service.registration;

import com.app.webapp.model.VerificationToken;

import java.util.Optional;

public interface IVerificationTokenService {
    Optional<VerificationToken> optionalFindByToken(String token);

    VerificationToken save(VerificationToken token);
    Optional<VerificationToken> findByToken(String token);
}
