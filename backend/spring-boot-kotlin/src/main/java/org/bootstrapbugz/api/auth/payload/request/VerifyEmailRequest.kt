package org.bootstrapbugz.api.auth.payload.request

import jakarta.validation.constraints.NotBlank

data class VerifyEmailRequest(@NotBlank(message = "{token.required}") val token: String)
