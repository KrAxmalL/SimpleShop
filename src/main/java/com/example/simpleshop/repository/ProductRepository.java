package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, BigInteger> {

    @Query("from Product p where p.productName = :targetProductName")
    Optional<Product> findProductByName(@Param("targetProductName") String productName);
}