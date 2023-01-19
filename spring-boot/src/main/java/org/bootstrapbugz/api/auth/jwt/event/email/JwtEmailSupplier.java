package org.bootstrapbugz.api.auth.jwt.event.email;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose;

public class JwtEmailSupplier {
  private static final Map<JwtPurpose, Supplier<JwtEmail>> emailType =
      new EnumMap<>(JwtPurpose.class);

  static {
    emailType.put(JwtPurpose.CONFIRM_REGISTRATION_TOKEN, ConfirmRegistrationEmail::new);
    emailType.put(JwtPurpose.FORGOT_PASSWORD_TOKEN, ForgotPasswordEmail::new);
  }

  public JwtEmail supplyEmail(JwtPurpose jwtPurpose) {
    final var emailSupplier = emailType.get(jwtPurpose);
    if (emailSupplier == null)
      throw new IllegalArgumentException("Invalid email type: " + jwtPurpose);
    return emailSupplier.get();
  }
}
