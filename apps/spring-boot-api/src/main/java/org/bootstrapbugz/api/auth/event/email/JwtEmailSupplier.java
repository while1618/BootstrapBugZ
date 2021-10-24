package org.bootstrapbugz.api.auth.event.email;

import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class JwtEmailSupplier {
  private static final Map<JwtUtil.JwtPurpose, Supplier<JwtEmail>> emailType =
      new EnumMap<>(JwtUtil.JwtPurpose.class);

  static {
    emailType.put(JwtUtil.JwtPurpose.CONFIRM_REGISTRATION_TOKEN, ConfirmRegistrationEmail::new);
    emailType.put(JwtUtil.JwtPurpose.FORGOT_PASSWORD_TOKEN, ResetPasswordEmail::new);
  }

  public JwtEmail supplyEmail(JwtUtil.JwtPurpose jwtPurpose) {
    final var emailSupplier = emailType.get(jwtPurpose);
    if (emailSupplier == null)
      throw new IllegalArgumentException("Invalid email type: " + jwtPurpose);
    return emailSupplier.get();
  }
}
