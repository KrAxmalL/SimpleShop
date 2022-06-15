package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.Order;
import org.springframework.data.repository.CrudRepository;

import java.math.BigInteger;

public interface OrderRepository extends CrudRepository<Order, BigInteger> {
}