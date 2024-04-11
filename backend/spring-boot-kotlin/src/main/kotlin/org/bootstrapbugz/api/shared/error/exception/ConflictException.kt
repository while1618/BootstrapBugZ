package org.bootstrapbugz.api.shared.error.exception

import java.io.Serial
import org.springframework.http.HttpStatus

class ConflictException : RuntimeException {
  val status: HttpStatus
  val field: String?

  constructor(field: String?, message: String?) : super(message) {
    status = HttpStatus.CONFLICT
    this.field = field
  }

  constructor(message: String?) : super(message) {
    status = HttpStatus.CONFLICT
    field = null
  }

  companion object {
    @Serial private val serialVersionUID = 8841655774286844538L
  }
}
