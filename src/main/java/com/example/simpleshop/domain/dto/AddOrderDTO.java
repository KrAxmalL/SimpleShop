package com.example.simpleshop.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddOrderDTO {

    private List<AddOrderItemDTO> orderItems;
    private BigInteger clientId;
}
