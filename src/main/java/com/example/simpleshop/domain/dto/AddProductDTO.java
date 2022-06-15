package com.example.simpleshop.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddProductDTO {

    private String productName;
    private BigDecimal productPrice;
    private Integer productQuantity;
}
