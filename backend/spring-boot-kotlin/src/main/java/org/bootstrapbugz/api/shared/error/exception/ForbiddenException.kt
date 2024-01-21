package org.bootstrapbugz.api.shared.error.exception

import org.springframework.http.HttpStatus
import java.io.Serial

class ForbiddenException(message: String?) : RuntimeException(message) {
    val status: HttpStatus = HttpStatus.FORBIDDEN

    companion object {
        @Serial
        private val serialVersionUID = 6438756026918720197L
    }
}
