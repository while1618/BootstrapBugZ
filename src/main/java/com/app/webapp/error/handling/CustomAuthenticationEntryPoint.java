package com.app.webapp.error.handling;

import com.app.webapp.dto.response.ErrorResponse;
import com.app.webapp.error.ErrorDomains;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getOutputStream()
                .println(String.valueOf(
                        new ErrorResponse(
                                HttpStatus.FORBIDDEN,
                                ErrorDomains.AUTH,
                                HttpStatus.FORBIDDEN.getReasonPhrase()))
                        );
    }
}
