package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.Order;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends CrudRepository<Order, BigInteger> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM client_order " +
                   "WHERE order_id = :target_order_id ",
            nativeQuery = true)
    void deleteOrder(@Param("target_order_id") BigInteger orderId);
}