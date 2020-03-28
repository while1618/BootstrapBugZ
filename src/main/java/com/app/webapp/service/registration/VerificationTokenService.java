package com.app.webapp.service.registration;

import com.app.webapp.model.VerificationToken;
import com.app.webapp.repository.VerificationTokenRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationTokenService implements IVerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenService(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken save(VerificationToken token) {
        return verificationTokenRepository.save(token);
    }

    @Override
    public Optional<VerificationToken> findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public Optional<VerificationToken> optionalFindByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }
}
