package org.bootstrapbugz.api.user.payload.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex
import org.bootstrapbugz.api.shared.validator.FieldMatch

@FieldMatch(first = "newPassword", second = "confirmNewPassword", message = "{passwords.doNotMatch}")
data class ChangePasswordRequest(
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val currentPassword: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val newPassword: String?,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val confirmNewPassword: String?
)
