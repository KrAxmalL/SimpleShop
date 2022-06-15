package com.example.simpleshop.controller;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.simpleshop.domain.dto.ErrorResponse;
import com.example.simpleshop.domain.dto.LoginPrincipalDTO;
import com.example.simpleshop.domain.model.Principal;
import com.example.simpleshop.domain.model.TokenPair;
import com.example.simpleshop.exceptions.InvalidTokenException;
import com.example.simpleshop.exceptions.TokenNotFoundException;
import com.example.simpleshop.security.JWTAuthorizationFilter;
import com.example.simpleshop.service.JWTTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JWTTokenService jwtTokenService;

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginPrincipalDTO principalToLogin) {
        try {
            final UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(principalToLogin.getEmail(), principalToLogin.getPassword());
            final Principal principal = (Principal) authenticationManager.authenticate(authenticationToken).getPrincipal();

            final TokenPair tokens = jwtTokenService.getNewTokens(principal);
            return ResponseEntity.ok().body(tokens);
        } catch(AuthenticationException ex) {
            log.info("Authentication failed: " + ex.getMessage());
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<Object> refreshTokens(@RequestHeader HttpHeaders headers) {
        try {
            final List<String> authorizationHeaderValues = headers.get(HttpHeaders.AUTHORIZATION);
            if(authorizationHeaderValues == null) {
                throw new TokenNotFoundException("No Authorization header present");
            }
            if(authorizationHeaderValues.size() == 0) {
                throw new TokenNotFoundException("No token present in Authorization header");
            }

            final String authHeaderValueStr = authorizationHeaderValues.get(0);
            if(authHeaderValueStr.length() < JWTAuthorizationFilter.BEARER_LENGTH) {
                throw new TokenNotFoundException("No token present in Authorization header");
            }

            final String refreshToken = authHeaderValueStr.substring(JWTAuthorizationFilter.BEARER_LENGTH);
            final TokenPair tokens = jwtTokenService.getRefreshedTokens(refreshToken);
            return ResponseEntity.ok().body(tokens);
        } catch (TokenExpiredException ex) {
            log.error("Error logging in: {}", ex.getMessage());
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (TokenNotFoundException | InvalidTokenException ex) {
            log.error("Error logging in: {}", ex.getMessage());
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (Exception ex) {
            log.error("Error logging in: {}", ex.getMessage());
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
