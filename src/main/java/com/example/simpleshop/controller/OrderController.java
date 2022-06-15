package com.example.simpleshop.controller;

import com.example.simpleshop.domain.dto.AddOrderDTO;
import com.example.simpleshop.domain.dto.ErrorResponse;
import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.model.Order;
import com.example.simpleshop.exceptions.AuthorizationException;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.service.OrderService;
import com.example.simpleshop.service.PrincipalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class OrderController {

    private final OrderService orderService;
    private final PrincipalService principalService;

    @PostMapping("")
    public ResponseEntity<Object> addNewOrder(@RequestBody AddOrderDTO addOrderDTO) {
        try {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final String clientEmail = authentication.getPrincipal().toString();
            final BigInteger clientId = principalService.getPrincipal(clientEmail).getId();

            addOrderDTO.setClientId(clientId);
            final OrderSummaryDTO addedOrder = orderService.addOrder(addOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedOrder);
        } catch(InvalidParameterException ex) {
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PatchMapping("/{orderId}/pay")
    public ResponseEntity<Object> payForOrder(@PathVariable(name = "orderId") BigInteger orderId) {
        try {
            final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            final String clientEmail = authentication.getPrincipal().toString();
            final BigInteger clientId = principalService.getPrincipal(clientEmail).getId();

            orderService.payForOrder(clientId, orderId);
            return ResponseEntity.noContent().build();
        } catch(InvalidParameterException ex) {
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch(AuthorizationException ex) {
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
