package org.bootstrapbugz.backend.auth.jwt.event.email;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bootstrapbugz.backend.auth.jwt.util.JwtUtil.JwtPurpose;

public class JwtEmailSupplier {
  private static final Map<JwtPurpose, Supplier<JwtEmail>> emailType =
      new EnumMap<>(JwtPurpose.class);

  static {
    emailType.put(JwtPurpose.VERIFY_EMAIL_TOKEN, VerificationEmail::new);
    emailType.put(JwtPurpose.RESET_PASSWORD_TOKEN, ResetPasswordEmail::new);
  }

  public JwtEmail supplyEmail(JwtPurpose jwtPurpose) {
    final var emailSupplier = emailType.get(jwtPurpose);
    if (emailSupplier == null)
      throw new IllegalArgumentException("Invalid email type: " + jwtPurpose);
    return emailSupplier.get();
  }
}
