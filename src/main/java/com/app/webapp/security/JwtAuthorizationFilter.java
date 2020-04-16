package com.app.webapp.security;

import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.error.handling.CustomFilterExceptionHandler;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final String secret;
    private final UserDetailsService userDetailsService;
    private final MessageSource messageSource;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, String secret,
                                  @Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService,
                                  MessageSource messageSource) {
        super(authenticationManager);
        this.secret = secret;
        this.userDetailsService = userDetailsService;
        this.messageSource = messageSource;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(JwtProperties.HEADER);
        if (header == null || !header.startsWith(JwtProperties.BEARER)) {
            chain.doFilter(request, response);
            return;
        }
        try {
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ResourceNotFound e) {
            CustomFilterExceptionHandler.handleFilterException(response, e.getMessage());
        }
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(JwtProperties.HEADER);
        if (token == null)
            throw new ResourceNotFound(messageSource.getMessage("authToken.notFound", null, LocaleContextHolder.getLocale()));

        String usernameOrEmail = getUsernameOrEmailFromToken(token);
        if (usernameOrEmail == null)
            throw new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(usernameOrEmail);
        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
    }

    private String getUsernameOrEmailFromToken(String token) {
        return JWT.require(Algorithm.HMAC512(secret))
                .build()
                .verify(token.replace(JwtProperties.BEARER, ""))
                .getSubject();
    }
}
