package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddOrderDTO;
import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.model.Order;

import java.math.BigInteger;

public interface OrderService {

    OrderSummaryDTO addOrder(AddOrderDTO addOrderDTO);

    void payForOrder(BigInteger clientId, BigInteger orderId);
}
