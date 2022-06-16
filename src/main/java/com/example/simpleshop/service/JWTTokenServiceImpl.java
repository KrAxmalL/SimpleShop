package com.example.simpleshop.service;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.simpleshop.domain.model.Principal;
import com.example.simpleshop.domain.model.TokenPair;
import com.example.simpleshop.exceptions.InvalidTokenException;
import com.example.simpleshop.exceptions.TokenNotFoundException;
import com.example.simpleshop.repository.PrincipalRepository;
import com.example.simpleshop.security.JWTManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class JWTTokenServiceImpl implements JWTTokenService {

    private final JWTManager jwtManager;

    private final PrincipalService principalService;
    private final PrincipalRepository principalRepository;

    @Override
    public TokenPair getNewTokens(Principal principal) {
        String accessToken = jwtManager.getAccessToken(principal);
        String refreshToken = jwtManager.getRefreshToken(principal);

        final BigInteger principalId = principal.getId();
        principalRepository.deleteTokenForPrincipal(principalId);
        principalRepository.addTokenForPrincipal(principalId, refreshToken);

        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public TokenPair getRefreshedTokens(String refreshToken) {
        try {
            final Optional<String> prevRefreshToken = principalRepository.getRefreshToken(refreshToken);
            if (prevRefreshToken.isPresent()) {
                final String email = getEmail(refreshToken);
                final Principal principal = principalService.getPrincipal(email);
                return getNewTokens(principal);
            } else {
                throw new TokenNotFoundException("Provided refresh token doesn't exist");
            }
        } catch(TokenExpiredException ex) {
            throw ex;
        } catch(JWTVerificationException ex) {
            throw new InvalidTokenException(ex.getMessage());
        }
    }

    @Override
    public String getEmail(String token) {
        try {
            return jwtManager.verifyToken(token)
                    .getSubject();
        } catch(TokenExpiredException ex) {
            throw ex;
        } catch(JWTVerificationException ex) {
            throw new InvalidTokenException(ex.getMessage());
        }
    }

    @Override
    public List<String> getRoles(String accessToken) {
        try {
            return jwtManager.verifyToken(accessToken)
                    .getClaim(JWTManager.CLAIM_ROLES)
                    .asList(String.class);
        } catch(TokenExpiredException ex) {
            throw ex;
        } catch(JWTVerificationException ex) {
            throw new InvalidTokenException(ex.getMessage());
        }
    }

    @Override
    public boolean isValidToken(String token) {
        try {
            jwtManager.verifyToken(token);
            return true;
        } catch(JWTVerificationException ex) {
            return false;
        }
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            jwtManager.verifyToken(token);
            return false;
        } catch(TokenExpiredException ex) {
            return true;
        } catch (JWTVerificationException ex) {
            throw new InvalidTokenException(ex.getMessage());
        }
    }
}
