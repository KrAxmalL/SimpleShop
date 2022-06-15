package com.example.simpleshop.service;

import com.example.simpleshop.domain.model.Principal;
import com.example.simpleshop.domain.model.Role;
import com.example.simpleshop.repository.PrincipalRepository;
import com.example.simpleshop.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PrincipalServiceImpl implements PrincipalService, UserDetailsService {

    private final PrincipalRepository principalRepository;
    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public Principal savePrincipal(Principal principal) {
        principal.setPassword(passwordEncoder.encode(principal.getPassword()));
        return principalRepository.save(principal);
    }

    @Override
    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public void addRoleToPrincipal(String email, String roleName) {
        Principal principal = principalRepository.findPrincipalByEmail(email);
        Role role = roleRepository.findRoleByName(roleName);

        principal.getAuthorities().add(role);
    }

    @Override
    public Principal getPrincipal(String email) {
        return principalRepository.findPrincipalByEmail(email);
    }

    @Override
    public Iterable<Principal> getPrincipals() {
        return principalRepository.findAll();
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
