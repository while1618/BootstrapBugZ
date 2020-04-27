package com.app.bootstrapbugz.error.handling;

import com.app.bootstrapbugz.dto.response.ErrorResponse;
import com.app.bootstrapbugz.error.ErrorDomains;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomFilterExceptionHandler {
    public static void handleException(HttpServletResponse response, String message) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    ErrorDomains.AUTH,
                    message
            );
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().println(errorResponse.toString());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
