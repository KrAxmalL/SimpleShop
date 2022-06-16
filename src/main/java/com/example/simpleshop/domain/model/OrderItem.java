package com.example.simpleshop.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "product_order")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderItem {

    @EmbeddedId
    private OrderItemId orderItemId;

    @Column(name = "product_quantity")
    private Integer productQuantity;

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", productQuantity=" + productQuantity +
                '}';
    }
}
