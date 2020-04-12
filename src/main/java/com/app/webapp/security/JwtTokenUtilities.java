package com.app.webapp.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenUtilities {
    @Value("${jwt.expirationInTime}")
    private Long jwtExpirationTime;
    private Algorithm algorithm;

    public JwtTokenUtilities(@Value("${jwt.secret}") String jwtSecret) {
        this.algorithm = Algorithm.HMAC256(jwtSecret);
    }

    public String generateToken(Authentication authentication) throws JWTCreationException {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            Date expiryDate = new Date(new Date().getTime() + jwtExpirationTime);
            return JWT.create()
                    .withSubject(userDetails.getUsername())
                    .withIssuedAt(new Date())
                    .withExpiresAt(expiryDate)
                    .sign(algorithm);
    }

    public String getUsernameFromJWT(String token) throws JWTDecodeException {
        return JWT.decode(token).getSubject();
    }

    public void validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm).build();
        verifier.verify(token);
    }
}
