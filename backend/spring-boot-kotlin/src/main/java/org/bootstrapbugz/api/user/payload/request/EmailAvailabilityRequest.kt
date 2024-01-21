package org.bootstrapbugz.api.user.payload.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.bootstrapbugz.api.shared.constants.Regex

data class EmailAvailabilityRequest(
        @NotBlank(message = "{email.required}")
        @field:Email(message = "{email.invalid}", regexp = Regex.EMAIL)
        val email: String?
)
