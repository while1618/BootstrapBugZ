package org.bootstrapbugz.api.shared.error.exception

import java.io.Serial
import org.springframework.http.HttpStatus

class BadRequestException(val field: String, message: String?) : RuntimeException(message) {
  val status: HttpStatus = HttpStatus.BAD_REQUEST

  companion object {
    @Serial private val serialVersionUID = -6237654540916338509L
  }
}
