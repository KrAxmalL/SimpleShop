package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.OrderItem;
import com.example.simpleshop.domain.model.OrderItemId;
import com.example.simpleshop.domain.model.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;

public interface OrderItemRepository extends CrudRepository<OrderItem, OrderItemId> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM product_order " +
                   "WHERE order_id = :target_order_id " +
                   "AND product_id = :target_product_id ",
           nativeQuery = true)
    void deleteOrderItem(@Param("target_order_id") BigInteger orderId,
                         @Param("target_product_id") BigInteger productId);
}