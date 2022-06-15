package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddOrderItemDTO;
import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.model.Order;
import com.example.simpleshop.domain.model.OrderItem;

import java.math.BigInteger;

public interface OrderItemService {

    OrderItem addOrderItem(Order order, AddOrderItemDTO addOrderItemDTO);
}
