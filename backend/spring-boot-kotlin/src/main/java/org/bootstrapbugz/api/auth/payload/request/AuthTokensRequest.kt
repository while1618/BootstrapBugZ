package org.bootstrapbugz.api.auth.payload.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex
import org.bootstrapbugz.api.shared.validator.UsernameOrEmail

data class AuthTokensRequest(
    @UsernameOrEmail(message = "{auth.invalid}")
    @NotBlank(message = "{usernameOrEmail.required}")
    val usernameOrEmail: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{auth.invalid}")
    val password: String?
)
