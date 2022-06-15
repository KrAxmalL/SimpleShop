package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddOrderDTO;
import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.model.Order;

public interface OrderService {

    OrderSummaryDTO addOrder(AddOrderDTO addOrderDTO);
}
