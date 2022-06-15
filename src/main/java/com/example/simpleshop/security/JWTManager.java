package com.example.simpleshop.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.simpleshop.domain.model.Principal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTManager {

    public static final String CLAIM_ROLES = "roles";

    private static final long ACCESS_TOKEN_EXPIRATION_TIME_MILLIS = 10 * 60 * 1000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME_MILLIS = 7 * 24 * 60 * 60 * 100;

    private static final String ISSUER = "SimpleShopApplication";

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JWTManager(@Value("${jwt.algorithm.secret}") String algorithmSecret) {
        this.algorithm = Algorithm.HMAC256(algorithmSecret.getBytes(StandardCharsets.UTF_8));
        this.verifier = JWT.require(algorithm).build();
    }

    public String getAccessToken(Principal principal) {
        return JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME_MILLIS))
                .withIssuer(ISSUER)
                .withClaim(CLAIM_ROLES, principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .sign(algorithm);
    }

    public String getRefreshToken(Principal principal) {
        return JWT.create()
                .withSubject(principal.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME_MILLIS))
                .withIssuer(ISSUER)
                .sign(algorithm);
    }

    public String getEmail(String token) {
        return verifyToken(token).getSubject();
    }

    public List<String> getRoles(String token) {
        return verifyToken(token).getClaim(CLAIM_ROLES).asList(String.class);
    }

    public DecodedJWT verifyToken(String token) {
        return verifier.verify(token);
    }
}
