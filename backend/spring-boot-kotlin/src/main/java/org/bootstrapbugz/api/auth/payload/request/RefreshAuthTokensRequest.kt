package org.bootstrapbugz.api.auth.payload.request

import jakarta.validation.constraints.NotBlank

data class RefreshAuthTokensRequest(@NotBlank(message = "{token.required}") val refreshToken: String?)
