package com.example.simpleshop.service;

import com.example.simpleshop.domain.model.Principal;
import com.example.simpleshop.domain.model.TokenPair;

import java.util.List;

public interface JWTTokenService {

    TokenPair getNewTokens(Principal principal);

    TokenPair getRefreshedTokens(String refreshToken);

    String getEmail(String token);

    List<String> getRoles(String accessToken);
}
