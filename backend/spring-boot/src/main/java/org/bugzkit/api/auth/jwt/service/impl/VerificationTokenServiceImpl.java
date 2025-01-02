package org.bugzkit.api.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import java.time.Instant;
import org.bugzkit.api.auth.jwt.event.OnSendJwtEmail;
import org.bugzkit.api.auth.jwt.service.VerificationTokenService;
import org.bugzkit.api.auth.jwt.util.JwtUtil;
import org.bugzkit.api.auth.jwt.util.JwtUtil.JwtPurpose;
import org.bugzkit.api.shared.error.exception.BadRequestException;
import org.bugzkit.api.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenServiceImpl implements VerificationTokenService {
  private static final JwtPurpose PURPOSE = JwtPurpose.VERIFY_EMAIL_TOKEN;
  private final ApplicationEventPublisher eventPublisher;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.verify-email-token.duration}")
  private int tokenDuration;

  public VerificationTokenServiceImpl(ApplicationEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

  @Override
  public String create(Long userId) {
    return JWT.create()
        .withIssuer(userId.toString())
        .withClaim("purpose", PURPOSE.name())
        .withIssuedAt(Instant.now())
        .withExpiresAt(Instant.now().plusSeconds(tokenDuration))
        .sign(JwtUtil.getAlgorithm(secret));
  }

  @Override
  public void check(String token) {
    try {
      JwtUtil.verify(token, secret, PURPOSE);
    } catch (RuntimeException e) {
      throw new BadRequestException("auth.tokenInvalid");
    }
  }

  @Override
  public void sendToEmail(User user, String token) {
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, PURPOSE));
  }
}
