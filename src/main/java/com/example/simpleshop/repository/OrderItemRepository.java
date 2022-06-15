package com.example.simpleshop.repository;

import com.example.simpleshop.domain.model.OrderItem;
import com.example.simpleshop.domain.model.OrderItemId;
import com.example.simpleshop.domain.model.Product;
import org.springframework.data.repository.CrudRepository;

public interface OrderItemRepository extends CrudRepository<OrderItem, OrderItemId> {
}