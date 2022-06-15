package com.example.simpleshop.security;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.example.simpleshop.domain.dto.ErrorResponse;
import com.example.simpleshop.exceptions.InvalidTokenException;
import com.example.simpleshop.service.JWTTokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    public static final String BEARER_STR = "Bearer ";
    public static final int BEARER_LENGTH = BEARER_STR.length();

    private static final List<String> ignoredPaths = List.of(
            "/api/authentication/login",
            "/api/authentication/refresh"
    );

    private final JWTTokenService jwtTokenService;

    @Autowired
    public JWTAuthorizationFilter(JWTTokenService jwtTokenService) {
        super();
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String path = request.getServletPath();
        if(ignoredPaths.contains(path)) {
            filterChain.doFilter(request, response);
        }
        else {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if(authorizationHeader != null && authorizationHeader.startsWith(BEARER_STR)) {
                try {
                    String accessToken = authorizationHeader.substring(BEARER_LENGTH);
                    String email = jwtTokenService.getEmail(accessToken);
                    List<String> roles = jwtTokenService.getRoles(accessToken);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(email, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch(TokenExpiredException ex) {
                    int responseStatus = HttpStatus.UNAUTHORIZED.value();
                    response.setStatus(responseStatus);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    final ErrorResponse errorResponse = new ErrorResponse(responseStatus, ex.getMessage());
                    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
                } catch(InvalidTokenException ex) {
                    int responseStatus = HttpStatus.FORBIDDEN.value();
                    response.setStatus(responseStatus);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    final ErrorResponse errorResponse = new ErrorResponse(responseStatus, ex.getMessage());
                    new ObjectMapper().writeValue(response.getWriter(), errorResponse);
                }
            }
            else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
