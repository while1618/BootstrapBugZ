package org.bootstrapbugz.api.user.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import org.bootstrapbugz.api.shared.constants.Regex

data class PatchProfileRequest(
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{firstName.invalid}")
    val firstName: String?,
    @field:Pattern(regexp = Regex.FIRST_AND_LAST_NAME, message = "{lastName.invalid}")
    val lastName: String?,
    @field:Pattern(regexp = Regex.USERNAME, message = "{username.invalid}")
    val username: String?,
    @field:Email(message = "{email.invalid}", regexp = Regex.EMAIL)
    val email: String?
)
