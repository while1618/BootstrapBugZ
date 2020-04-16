package com.app.webapp.error.handling;

import com.app.webapp.dto.response.ErrorResponse;
import com.app.webapp.error.ErrorDomains;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomFilterExceptionHandler {
    public static void handleFilterException(HttpServletResponse response, String message) {
        try {
            response.setContentType("application/json");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getOutputStream().println(
                    String.valueOf(new ErrorResponse(
                            HttpStatus.UNAUTHORIZED,
                            ErrorDomains.AUTH,
                            message)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
