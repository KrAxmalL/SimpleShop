package com.example.simpleshop.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "client_order")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private BigInteger orderId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "paid")
    private Boolean paid;

    @OneToMany
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems;

    //Using client id instead of principal object
    @Column(name = "client_id")
    private BigInteger clientId;
}
