package com.app.webapp.error.handling;

import com.app.webapp.dto.response.ErrorResponse;
import com.app.webapp.error.ErrorDomains;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final MessageSource messageSource;

    public CustomAuthenticationEntryPoint(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getOutputStream()
                .println(
                        new ErrorResponse(
                                HttpStatus.FORBIDDEN,
                                ErrorDomains.AUTH,
                                messageSource.getMessage("user.notAuthorized", null, LocaleContextHolder.getLocale())
                        ).toString());
    }
}
