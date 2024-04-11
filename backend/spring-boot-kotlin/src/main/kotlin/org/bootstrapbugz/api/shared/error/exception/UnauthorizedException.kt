package org.bootstrapbugz.api.shared.error.exception

import java.io.Serial
import org.springframework.http.HttpStatus

class UnauthorizedException(message: String?) : RuntimeException(message) {
  val status: HttpStatus = HttpStatus.UNAUTHORIZED

  companion object {
    @Serial private val serialVersionUID = -8525346226722308705L
  }
}
