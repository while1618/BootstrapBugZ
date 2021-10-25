package org.bootstrapbugz.api.auth.jwt.service.impl;

import com.auth0.jwt.JWT;
import org.bootstrapbugz.api.auth.jwt.event.OnSendJwtEmail;
import org.bootstrapbugz.api.auth.jwt.service.ForgotPasswordTokenService;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;
import org.bootstrapbugz.api.auth.jwt.redis.repository.UserBlacklistRepository;
import org.bootstrapbugz.api.shared.error.exception.ForbiddenException;
import org.bootstrapbugz.api.shared.message.service.MessageService;
import org.bootstrapbugz.api.user.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class ForgotPasswordTokenServiceImpl implements ForgotPasswordTokenService {
  private static final JwtUtil.JwtPurpose PURPOSE = JwtUtil.JwtPurpose.FORGOT_PASSWORD_TOKEN;

  private final UserBlacklistRepository userBlacklistRepository;
  private final MessageService messageService;
  private final ApplicationEventPublisher eventPublisher;

  @Value("${jwt.secret}")
  private String secret;

  @Value("${jwt.forgot-password-token.duration}")
  private int tokenDuration;

  public ForgotPasswordTokenServiceImpl(
      UserBlacklistRepository userBlacklistRepository,
      MessageService messageService,
      ApplicationEventPublisher eventPublisher) {
    this.userBlacklistRepository = userBlacklistRepository;
    this.messageService = messageService;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public String create(Long userId) {
    return JWT.create()
        .withClaim("userId", userId)
        .withClaim("issuedAt", Instant.now().toString())
        .withClaim("purpose", PURPOSE.name())
        .withExpiresAt(new Date(System.currentTimeMillis() + tokenDuration * 1000L))
        .sign(JwtUtil.getAlgorithm(secret));
  }

  @Override
  public void check(String token) {
    JwtUtil.verify(token, secret, PURPOSE);
    isInUserBlacklist(token);
  }

  private void isInUserBlacklist(String token) {
    final var userInBlacklist = userBlacklistRepository.findById(JwtUtil.getUserId(token));
    if (userInBlacklist.isPresent()
        && Instant.parse(JwtUtil.getIssuedAt(token)).isBefore(userInBlacklist.get().getUpdatedAt()))
      throw new ForbiddenException(messageService.getMessage("token.invalid"));
  }

  @Override
  public void sendToEmail(User user, String token) {
    eventPublisher.publishEvent(new OnSendJwtEmail(user, token, PURPOSE));
  }
}
