package org.bootstrapbugz.api.auth.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex
import org.bootstrapbugz.api.shared.validator.FieldMatch

@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
data class RegisterUserRequest(
    @NotBlank(message = "{firstName.required}")
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
    val firstName: String?,
    @NotBlank(message = "{lastName.required}")
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
    val lastName: String?,
    @NotBlank(message = "{username.required}")
    @field:Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
    val username: String?,
    @NotBlank(message = "{email.required}")
    @field:Email(message = "{email.invalid}", regexp = Regex.EMAIL)
    val email: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val password: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val confirmPassword: String?
)
