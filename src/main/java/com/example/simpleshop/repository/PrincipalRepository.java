package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.Principal;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface PrincipalRepository extends CrudRepository<Principal, BigInteger> {

    @Query(value = "FROM Principal u WHERE u.email = :email")
    Principal findPrincipalByEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM principal_jwt WHERE principal_id = :target_principal_id",
            nativeQuery = true)
    void deleteTokenForPrincipal(@Param("target_principal_id") BigInteger principalId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "INSERT INTO principal_jwt(principal_id, token) " +
            "VALUES(:target_principal_id, :target_token)",
            nativeQuery = true)
    void addTokenForPrincipal(@Param("target_principal_id") BigInteger principalId,
                              @Param("target_token") String refreshToken);

    @Query(value = "SELECT token FROM principal_jwt " +
            "WHERE token = :target_token",
            nativeQuery = true)
    Optional<String> getRefreshToken(@Param("target_token") String refreshToken);
}