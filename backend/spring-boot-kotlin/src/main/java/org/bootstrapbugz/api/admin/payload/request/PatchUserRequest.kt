package org.bootstrapbugz.api.admin.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex
import org.bootstrapbugz.api.shared.validator.FieldMatch
import org.bootstrapbugz.api.user.model.Role.RoleName

@FieldMatch(first = "password", second = "confirmPassword", message = "{passwords.doNotMatch}")
data class PatchUserRequest(
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
    val firstName: String?,
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
    val lastName: String?,
    @field:Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
    val username: String?,
    @field:Email(message = "{email.invalid}", regexp = Regex.EMAIL)
    val email: String?,
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val password: String?,
    @field:Pattern(regexp = Regex.PASSWORD, message = "{password.invalid}")
    val confirmPassword: String?,
    val active: Boolean?,
    val lock: Boolean?,
    val roleNames: Set<RoleName>?
)
