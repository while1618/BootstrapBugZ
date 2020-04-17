package com.app.webapp.security;

import com.app.webapp.dto.request.LoginRequest;
import com.app.webapp.error.exception.ResourceNotFound;
import com.app.webapp.error.exception.UserNotActivatedException;
import com.app.webapp.error.handling.CustomFilterExceptionHandler;
import com.app.webapp.repository.UserRepository;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final String secret;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final MessageSource messageSource;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, String secret, UserRepository userRepository,
                                   MessageSource messageSource) {
        this.authenticationManager = authenticationManager;
        this.secret = secret;
        this.userRepository = userRepository;
        this.messageSource = messageSource;
        this.setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsernameOrEmail(),
                    loginRequest.getPassword(),
                    new ArrayList<>()
            );
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            checkIfUserIsActivated(loginRequest.getUsernameOrEmail());
            return authentication;
        } catch (IOException | AuthenticationException | ResourceNotFound e) {
            CustomFilterExceptionHandler.handleFilterException(response,
                    messageSource.getMessage("login.badCredentials", null, LocaleContextHolder.getLocale()));
        } catch (UserNotActivatedException e) {
            CustomFilterExceptionHandler.handleFilterException(response, e.getMessage());
        }
        return null;
    }

    private void checkIfUserIsActivated(String usernameOrEmail) {
        com.app.webapp.model.User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
                () -> new ResourceNotFound(messageSource.getMessage("user.notFound", null, LocaleContextHolder.getLocale())));
        if (!user.isActivated())
            throw new UserNotActivatedException(messageSource.getMessage("user.notActivated", null, LocaleContextHolder.getLocale()));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication auth) throws IOException, ServletException {
        String token = JWT.create()
                .withSubject(((User) auth.getPrincipal()).getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret));
        response.addHeader(JwtProperties.HEADER, JwtProperties.BEARER + token);
    }
}
