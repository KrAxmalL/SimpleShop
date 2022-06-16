package com.example.simpleshop.service;

import com.example.simpleshop.domain.model.Principal;
import com.example.simpleshop.repository.PrincipalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrincipalServiceImpl implements PrincipalService, UserDetailsService {

    private final PrincipalRepository principalRepository;

    @Override
    public Principal getPrincipal(String email) {
        return principalRepository.findPrincipalByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserDetails principal = principalRepository.findPrincipalByEmail(email);
        if(principal == null) {
            throw new UsernameNotFoundException("Principal not found");
        }
        return principal;
    }
}
