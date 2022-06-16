package com.example.simpleshop.domain.mappers;

import com.example.simpleshop.domain.dto.OrderItemSummaryDTO;
import com.example.simpleshop.domain.model.OrderItem;

public class OrderItemMapper {

    public static OrderItemSummaryDTO toOrderItemSummaryDTO(OrderItem orderItem) {
        OrderItemSummaryDTO orderItemSummaryDTO = new OrderItemSummaryDTO();
        orderItemSummaryDTO.setProductId(orderItem.getOrderItemId().getProduct().getProductId());
        orderItemSummaryDTO.setProductQuantity(orderItem.getProductQuantity());
        return orderItemSummaryDTO;
    }
}
