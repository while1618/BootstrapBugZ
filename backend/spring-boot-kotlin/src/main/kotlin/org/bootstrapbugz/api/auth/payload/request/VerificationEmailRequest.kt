package org.bootstrapbugz.api.auth.payload.request

import jakarta.validation.constraints.NotBlank
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail

data class VerificationEmailRequest(
    @UsernameOrEmail(message = "{usernameOrEmail.invalid}")
    @NotBlank(message = "{usernameOrEmail.required}")
    val usernameOrEmail: String
)
