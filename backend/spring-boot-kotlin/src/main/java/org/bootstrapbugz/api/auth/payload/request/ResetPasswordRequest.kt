package org.bootstrapbugz.api.auth.payload.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex
import org.bootstrapbugz.api.shared.validator.FieldMatch

@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
data class ResetPasswordRequest(
    @NotBlank(message = "{token.required}")
    val token: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(
        regexp = Regex.PASSWORD,
        message = "{password.invalid}"
    )
    val password: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val confirmPassword: String?
)
