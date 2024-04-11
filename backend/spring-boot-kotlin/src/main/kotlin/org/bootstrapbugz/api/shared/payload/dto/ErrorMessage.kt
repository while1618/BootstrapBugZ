package org.bootstrapbugz.api.shared.payload.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.springframework.http.HttpStatus

class ErrorMessage(status: HttpStatus) {
  private val timestamp: LocalDateTime = LocalDateTime.now()
  private val status: Int = status.value()
  private val error: String = status.reasonPhrase
  private val details: MutableList<Detail> = ArrayList()

  fun addDetails(message: String) {
    details.add(Detail(message))
  }

  fun addDetails(field: String, message: String) {
    details.add(Detail(field, message))
  }

  override fun toString(): String {
    return GsonBuilder()
      .setPrettyPrinting()
      .registerTypeAdapter(
        LocalDateTime::class.java,
        JsonSerializer<LocalDateTime> { localDateTime, _, _ ->
          JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_DATE_TIME))
        }
      )
      .create()
      .toJson(this)
  }

  data class Detail(
    val message: String,
    @JsonInclude(JsonInclude.Include.NON_NULL) val field: String? = null
  ) {
    override fun toString(): String = Gson().toJson(this)
  }
}
