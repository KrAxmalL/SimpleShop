package com.example.simpleshop.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class OrderItemSummaryDTO {

    private BigInteger productId;
    private Integer productQuantity;
}
