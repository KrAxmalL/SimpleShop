package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;

public interface RoleRepository extends JpaRepository<Role, BigInteger> {

    @Query(value = "FROM Role WHERE roleName = :roleName")
    Role findRoleByName(@Param("roleName") String roleName);
}