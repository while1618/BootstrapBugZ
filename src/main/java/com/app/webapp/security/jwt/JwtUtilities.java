package com.app.webapp.security.jwt;

import com.app.webapp.model.user.User;
import com.app.webapp.security.user.UserPrincipal;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Component
public class JwtUtilities {
    @Value("${jwt.serverSecret}")
    private String serverSecret;

    public String createToken(User user, String usage) throws JWTCreationException {
        String secret = createSecret(user.getUpdatedAt(), user.getLogoutFromAllDevicesAt(), usage);
        return createToken(user.getUsername(), secret);
    }

    public void checkToken(String token, User user, String usage) throws JWTVerificationException {
        String secret = createSecret(user.getUpdatedAt(), user.getLogoutFromAllDevicesAt(), usage);
        checkToken(token, secret);
    }

    public String createToken(UserPrincipal userPrincipal, String usage) throws JWTCreationException {
        String secret = createSecret(userPrincipal.getUpdatedAt(), userPrincipal.getLogoutFromAllDevicesAt(), usage);
        return createToken(userPrincipal.getUsername(), secret);
    }

    public void checkToken(String token, UserPrincipal userPrincipal, String usage) throws JWTVerificationException {
        String secret = createSecret(userPrincipal.getUpdatedAt(), userPrincipal.getLogoutFromAllDevicesAt(), usage);
        checkToken(token, secret);
    }

    private String createSecret(LocalDateTime updatedAt, LocalDateTime logoutFromAllDevicesAt, String usage) {
        return serverSecret +
                "." + updatedAt.format(DateTimeFormatter.ISO_DATE_TIME) +
                "." + logoutFromAllDevicesAt.format(DateTimeFormatter.ISO_DATE_TIME) +
                "." + usage;
    }

    private String createToken(String username, String secret) throws JWTCreationException {
        return JWT.create()
                .withSubject(username)
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME))
                .sign(Algorithm.HMAC512(secret.getBytes()));
    }

    private void checkToken(String token, String secret) throws JWTVerificationException {
        JWT.require(Algorithm.HMAC512(secret.getBytes()))
                .build()
                .verify(token.replace(JwtProperties.BEARER, ""));
    }

    public String getSubject(String token) throws JWTDecodeException, IllegalArgumentException {
        return JWT.decode(token.replace(JwtProperties.BEARER, ""))
                .getSubject();
    }
}
