package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddOrderDTO;
import com.example.simpleshop.domain.dto.AddOrderItemDTO;
import com.example.simpleshop.domain.dto.OrderSummaryDTO;
import com.example.simpleshop.domain.mappers.OrderMapper;
import com.example.simpleshop.domain.model.Order;
import com.example.simpleshop.domain.model.OrderItem;
import com.example.simpleshop.domain.model.OrderItemId;
import com.example.simpleshop.domain.model.Product;
import com.example.simpleshop.exceptions.AuthorizationException;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.repository.OrderItemRepository;
import com.example.simpleshop.repository.OrderRepository;
import com.example.simpleshop.repository.PrincipalRepository;
import com.example.simpleshop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final PrincipalRepository principalRepository;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler;

    private final DeleteUnpaidOrderService deleteUnpaidOrderService;

    private static final long UNPAID_ORDER_DELETION_DELAY_MILLIS = 10 * 60 * 1000;

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
        threadPoolTaskScheduler.schedule(new DeleteUnpaidOrderTask(deleteUnpaidOrderService, orderToAdd.getOrderId()),
                new Date(System.currentTimeMillis() + UNPAID_ORDER_DELETION_DELAY_MILLIS));

        return OrderMapper.toOrderSummaryDTO(orderToAdd);
    }

    @Override
    public void payForOrder(BigInteger clientId, BigInteger orderId) {
        if(orderId == null) {
            throw new InvalidParameterException("Order id can't be null");
        }
        final Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new InvalidParameterException("Order with provided id doesn't exist"));

        if(clientId == null) {
            throw new InvalidParameterException("Client id can't be null");
        }
        if(order.getClientId().compareTo(clientId) != 0) {
            throw new AuthorizationException("Client can't pay for orders of other clients");
        }

        if(order.getPaid()) {
            throw new InvalidParameterException("Client has already paid for order");
        }

        order.setPaid(true);
    }
}

//Moved to separate class to make deletion transactional in other thread
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class DeleteUnpaidOrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public void deleteUnpaidOrder(BigInteger orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        orderOpt.ifPresent((order) -> {
            if(!order.getPaid()) {
                for(OrderItem orderItem: order.getOrderItems()) {
                    final Product product = orderItem.getOrderItemId().getProduct();
                    product.setProductQuantity(product.getProductQuantity() + orderItem.getProductQuantity());
                    productRepository.save(product);
                    orderItemRepository.deleteOrderItem(orderItem.getOrderItemId().getOrder().getOrderId(),
                            orderItem.getOrderItemId().getProduct().getProductId());
                }
                orderRepository.deleteOrder(orderId);
            }
        });
    }
}

@RequiredArgsConstructor
@Slf4j
class DeleteUnpaidOrderTask implements Runnable {

    private final DeleteUnpaidOrderService deleteUnpaidOrderService;
    private final BigInteger orderId;

    @Override
    public void run() {
        log.info("Scheduled deletion of unpaid order");
        deleteUnpaidOrderService.deleteUnpaidOrder(orderId);
    }
}
