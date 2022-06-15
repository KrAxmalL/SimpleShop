package com.example.simpleshop.domain.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import java.math.BigInteger;

@Entity
@Table(name = "role")
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private BigInteger id;

    @Column(name = "role_name")
    private String roleName;

    @Override
    public String getAuthority() {
        return roleName;
    }

    public void setAuthority(String authority) { this.roleName = authority; }

    @Override
    public String toString() {
        return roleName;
    }
}
