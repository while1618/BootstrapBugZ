package org.bootstrapbugz.api.auth.jwt.event.email;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil;

public class JwtEmailSupplier {
  private static final Map<JwtUtil.JwtPurpose, Supplier<JwtEmail>> emailType =
      new EnumMap<>(JwtUtil.JwtPurpose.class);

  static {
    emailType.put(JwtUtil.JwtPurpose.CONFIRM_REGISTRATION_TOKEN, ConfirmRegistrationEmail::new);
    emailType.put(JwtUtil.JwtPurpose.FORGOT_PASSWORD_TOKEN, ForgotPasswordEmail::new);
  }

  public JwtEmail supplyEmail(JwtUtil.JwtPurpose jwtPurpose) {
    final var emailSupplier = emailType.get(jwtPurpose);
    if (emailSupplier == null)
      throw new IllegalArgumentException("Invalid email type: " + jwtPurpose);
    return emailSupplier.get();
  }
}
