package org.bootstrapbugz.api.shared.error.exception

import java.io.Serial
import org.springframework.http.HttpStatus

class ResourceNotFoundException(message: String?) : RuntimeException(message) {
  val status: HttpStatus = HttpStatus.NOT_FOUND

  companion object {
    @Serial private val serialVersionUID = -6147521296995365840L
  }
}
