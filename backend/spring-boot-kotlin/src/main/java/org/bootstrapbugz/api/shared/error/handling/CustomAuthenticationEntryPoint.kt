package org.bootstrapbugz.api.shared.error.handling

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.bootstrapbugz.api.shared.payload.dto.ErrorMessage
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException

@Component
class CustomAuthenticationEntryPoint : AuthenticationEntryPoint {

    companion object {
        private val log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)
    }

    override fun commence(request: HttpServletRequest?, response: HttpServletResponse, e: AuthenticationException) {
        try {
            val errorMessage = ErrorMessage(HttpStatus.UNAUTHORIZED).apply {
                addDetails(e.message ?: "Unauthorized")
            }
            response.contentType = MediaType.APPLICATION_PROBLEM_JSON_VALUE
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.outputStream.println(errorMessage.toString())
        } catch (ex: IOException) {
            log.error(ex.message ?: "An error occurred while processing the authentication entry point.")
        }
    }
}
