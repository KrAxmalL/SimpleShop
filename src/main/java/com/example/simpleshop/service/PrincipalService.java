package com.example.simpleshop.service;

import com.example.simpleshop.domain.model.Principal;
import com.example.simpleshop.domain.model.Role;

import java.util.List;

public interface PrincipalService {

    Principal savePrincipal(Principal principal);

    Role saveRole(Role role);

    void addRoleToPrincipal(String email, String roleName);

    Principal getPrincipal(String email);

    Iterable<Principal> getPrincipals();
}
