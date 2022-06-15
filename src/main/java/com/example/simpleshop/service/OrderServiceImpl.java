package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddOrderDTO;
import com.example.simpleshop.domain.dto.AddOrderItemDTO;
import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.mappers.OrderMapper;
import com.example.simpleshop.domain.model.Order;
import com.example.simpleshop.domain.model.OrderItem;
import com.example.simpleshop.domain.model.OrderItemId;
import com.example.simpleshop.domain.model.Product;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.repository.OrderItemRepository;
import com.example.simpleshop.repository.OrderRepository;
import com.example.simpleshop.repository.PrincipalRepository;
import com.example.simpleshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final PrincipalRepository principalRepository;

    @Override
    public OrderSummaryDTO addOrder(AddOrderDTO addOrderDTO) {
        if(addOrderDTO == null) {
            throw new InvalidParameterException("Order must not be null");
        }

        final BigInteger clientId = addOrderDTO.getClientId();
        if(clientId == null) {
            throw new InvalidParameterException("Client id must not be empty");
        }
        if(!principalRepository.existsById(clientId)) {
            throw new InvalidParameterException("Client with provided id doesn't exist");
        }

        final List<AddOrderItemDTO> orderItems = addOrderDTO.getOrderItems();
        if(orderItems == null) {
            throw new InvalidParameterException("Order items list must not be null");
        }
        if(orderItems.size() == 0) {
            throw new InvalidParameterException("Order items list must not be empty");
        }

        Order orderToAdd = new Order();
        orderToAdd.setCreatedAt(LocalDateTime.now());
        orderToAdd.setPaid(false);
        orderToAdd.setClientId(clientId);
        final List<OrderItem> orderItemsToAdd = new ArrayList<>();
        orderToAdd.setOrderItems(orderItemsToAdd);
        orderRepository.save(orderToAdd);

        for(AddOrderItemDTO addOrderItemDTO: orderItems) {
            if(addOrderItemDTO == null) {
                throw new InvalidParameterException("Order item must not be null");
            }

            final BigInteger productId = addOrderItemDTO.getProductId();
            if(productId == null) {
                throw new InvalidParameterException("Product id must not be null");
            }
            boolean sameProductInList = orderItems.stream()
                    .filter(orderItemToAdd -> orderItemToAdd.getProductId().compareTo(productId) == 0)
                    .count() > 1;
            if(sameProductInList) {
                throw new InvalidParameterException("Products in order must be unique");
            }

            final Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new InvalidParameterException("Product with provided id doesn't exist"));
            final Integer productQuantity = addOrderItemDTO.getProductQuantity();
            if(productQuantity == null) {
                throw new InvalidParameterException("Product quantity must not be null");
            }
            if(productQuantity.compareTo(0) < 1) {
                throw new InvalidParameterException("Product quantity must be greater than 0");
            }
            if(product.getProductQuantity() < productQuantity) {
                throw new InvalidParameterException("Product quantity is greater than amount of available products");
            }
            product.setProductQuantity(product.getProductQuantity() - productQuantity);
            productRepository.save(product);

            OrderItem orderItemToAdd = new OrderItem();
            OrderItemId orderItemId = new OrderItemId();
            orderItemId.setOrder(orderToAdd);
            orderItemId.setProduct(product);
            orderItemToAdd.setProductQuantity(productQuantity);
            orderItemToAdd.setOrderItemId(orderItemId);
            orderItemRepository.save(orderItemToAdd);

            orderItemsToAdd.add(orderItemToAdd);
        }
        return OrderMapper.toOrderSummaryDTO(orderToAdd);
    }
}
