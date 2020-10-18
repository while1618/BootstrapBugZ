package com.app.bootstrapbugz.error.handling;

import com.app.bootstrapbugz.error.response.ErrorResponse;
import com.app.bootstrapbugz.error.ErrorDomain;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public final class CustomFilterExceptionHandler {
    private CustomFilterExceptionHandler() {}

    public static void handleException(HttpServletResponse response, String message) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.UNAUTHORIZED,
                    ErrorDomain.AUTH,
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
