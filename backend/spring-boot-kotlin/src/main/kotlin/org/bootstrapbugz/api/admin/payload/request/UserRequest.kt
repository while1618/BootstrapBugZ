package org.bootstrapbugz.api.admin.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex
import org.bootstrapbugz.api.shared.validator.FieldMatch
import org.bootstrapbugz.api.user.model.Role.RoleName

@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
data class UserRequest(
    @NotBlank(message = "{firstName.required}")
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
    val firstName: String,
    @NotBlank(message = "{lastName.required}")
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
    val lastName: String,
    @NotBlank(message = "{username.required}")
    @field:Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
    val username: String,
    @NotBlank(message = "{email.required}")
    @field:Email(message = "{email.invalid}", regexp = Regex.EMAIL)
    val email: String,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val password: String,
    @NotBlank(message = "{password.required}")
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val confirmPassword: String,
    @NotNull(message = "{user.active.required}")
    val active: Boolean,
    @NotNull(message = "{user.lock.required}")
    val lock: Boolean,
    @NotEmpty(message = "{roles.empty}")
    val roleNames: MutableSet<RoleName>
)
