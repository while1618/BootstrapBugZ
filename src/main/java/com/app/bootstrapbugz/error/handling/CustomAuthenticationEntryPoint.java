package com.app.bootstrapbugz.error.handling;

import com.app.bootstrapbugz.dto.response.ErrorResponse;
import com.app.bootstrapbugz.constant.ErrorDomains;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) {
        try {
            final ErrorResponse errorResponse = new ErrorResponse(
                    HttpStatus.FORBIDDEN,
                    ErrorDomains.AUTH,
                    HttpStatus.FORBIDDEN.getReasonPhrase()
            );
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getOutputStream().println(errorResponse.toString());
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
