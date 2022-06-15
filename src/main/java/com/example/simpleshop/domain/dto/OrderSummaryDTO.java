package com.example.simpleshop.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderSummaryDTO {

    private BigInteger orderId;
    private LocalDateTime createdAt;
    private Boolean paid;

    List<OrderItemSummaryDTO> orderItems;
}
