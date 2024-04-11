package org.bootstrapbugz.api.auth.jwt.event.email

import java.util.*
import org.bootstrapbugz.api.auth.jwt.util.JwtUtil.JwtPurpose

object JwtEmailSupplier {
  private val emailType: Map<JwtPurpose, () -> JwtEmail> =
    mapOf(
      JwtPurpose.VERIFY_EMAIL_TOKEN to ::VerificationEmail,
      JwtPurpose.RESET_PASSWORD_TOKEN to ::ResetPasswordEmail
    )

  fun supplyEmail(jwtPurpose: JwtPurpose): JwtEmail =
    emailType[jwtPurpose]?.invoke()
      ?: throw IllegalArgumentException("Invalid email type: $jwtPurpose")
}
