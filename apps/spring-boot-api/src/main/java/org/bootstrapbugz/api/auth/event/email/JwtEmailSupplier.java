package org.bootstrapbugz.api.auth.event.email;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;
import org.bootstrapbugz.api.auth.util.JwtUtil.JwtPurpose;

public class JwtEmailSupplier {
  private static final Map<JwtPurpose, Supplier<JwtEmail>> emailType =
      new EnumMap<>(JwtPurpose.class);

  static {
    emailType.put(JwtPurpose.CONFIRM_REGISTRATION, ConfirmRegistrationEmail::new);
    emailType.put(JwtPurpose.FORGOT_PASSWORD, ResetPasswordEmail::new);
  }

  public JwtEmail supplyEmail(JwtPurpose jwtPurpose) {
    final var emailSupplier = emailType.get(jwtPurpose);
    if (emailSupplier == null)
      throw new IllegalArgumentException("Invalid player type: " + jwtPurpose);
    return emailSupplier.get();
  }
}
