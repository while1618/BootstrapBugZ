package com.app.webapp.service.impl;

import com.app.webapp.model.VerificationToken;
import com.app.webapp.repository.VerificationTokenRepository;
import com.app.webapp.service.VerificationTokenService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenServiceImpl(VerificationTokenRepository verificationTokenRepository) {
        this.verificationTokenRepository = verificationTokenRepository;
    }

    @Override
    public VerificationToken save(VerificationToken token) {
        return verificationTokenRepository.save(token);
    }

    @Override
    public void deactivate(VerificationToken token) {
        token.setUsed(true);
        verificationTokenRepository.save(token);
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
