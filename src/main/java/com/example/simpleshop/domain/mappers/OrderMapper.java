package com.example.simpleshop.domain.mappers;

import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.model.Order;

import java.util.stream.Collectors;

public class OrderMapper {

    public static OrderSummaryDTO toOrderSummaryDTO(Order order) {
        OrderSummaryDTO orderSummaryDTO = new OrderSummaryDTO();
        orderSummaryDTO.setOrderId(order.getOrderId());
        orderSummaryDTO.setCreatedAt(order.getCreatedAt());
        orderSummaryDTO.setPaid(order.getPaid());
        orderSummaryDTO.setOrderItems(order.getOrderItems().stream()
                                           .map(OrderItemMapper::toOrderItemSummaryDTO)
                                           .collect(Collectors.toList())
        );
        return orderSummaryDTO;
    }
}
