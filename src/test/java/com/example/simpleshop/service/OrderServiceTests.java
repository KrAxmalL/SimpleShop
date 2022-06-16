package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddOrderDTO;
import com.example.simpleshop.domain.dto.AddOrderItemDTO;
import com.example.simpleshop.domain.model.Order;
import com.example.simpleshop.domain.model.Product;
import com.example.simpleshop.exceptions.AuthorizationException;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.repository.OrderItemRepository;
import com.example.simpleshop.repository.OrderRepository;
import com.example.simpleshop.repository.PrincipalRepository;
import com.example.simpleshop.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class OrderServiceTests {

    @TestConfiguration
    static class OrderServiceTestsConfiguration {

        @Bean
        public OrderRepository orderRepository() {

            OrderRepository orderRepository = Mockito.mock(OrderRepository.class);
            Mockito.when(orderRepository.findById(BigInteger.ONE))
                    .thenReturn(Optional.of(new Order(BigInteger.ONE, LocalDateTime.now(), true, null, BigInteger.ONE)));
            Mockito.when(orderRepository.findById(BigInteger.TWO))
                    .thenReturn(Optional.empty());
            return orderRepository;
        }

        @Bean
        public ProductRepository productRepository() {
            ProductRepository productRepository = Mockito.mock(ProductRepository.class);
            Mockito.when(productRepository.findById(BigInteger.ONE))
                    .thenReturn(Optional.of(new Product(BigInteger.ONE, null, null, 5)));
            Mockito.when(productRepository.findById(BigInteger.TWO))
                    .thenReturn(Optional.empty());
            return productRepository;
        }

        @Bean
        public OrderItemRepository orderItemRepository() {
            return Mockito.mock(OrderItemRepository.class);
        }

        @Bean
        public PrincipalRepository principalRepository() {
            PrincipalRepository principalRepository = Mockito.mock(PrincipalRepository.class);
            Mockito.when(principalRepository.existsById(BigInteger.ONE))
                    .thenReturn(true);
            Mockito.when(principalRepository.existsById(BigInteger.TWO))
                    .thenReturn(false);
            return principalRepository;
        }

        @Bean
        public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
            return Mockito.mock(ThreadPoolTaskScheduler.class);
        }

        @Bean
        public DeleteUnpaidOrderService deleteUnpaidOrderService() {
            return Mockito.mock(DeleteUnpaidOrderService.class);
        }

        @Bean
        public OrderService orderService() {
            return new OrderServiceImpl(orderRepository(), productRepository(),
                    orderItemRepository(), principalRepository(),
                    threadPoolTaskScheduler(), deleteUnpaidOrderService());
        }
    }

    @Autowired
    private OrderService orderService;

    @Test
    public void addOrder_whenNullOrderThenException() {
        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(null));
    }

    @Test
    public void addOrder_whenNullClientIdThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, 1));
        orderItems.add(new AddOrderItemDTO(BigInteger.TWO, 1));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(null);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenClientDoesntExistThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, 1));
        orderItems.add(new AddOrderItemDTO(BigInteger.TWO, 1));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.TWO);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenNullOrderItemsThenException() {
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(null);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenEmptyOrderItemsThenException() {
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(Collections.emptyList());

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenNullOrderItemThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(null);
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenNullProductIdInOrderItemThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(null, 1));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenProductIdDuplicatedInOrderItemThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, 1));
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, 5));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenProductDoesntExistInOrderItemThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.TWO, 1));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenNullProductQuantityInOrderItemThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, null));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenProductQuantityLessEqualZeroInOrderItemThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, 0));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void addOrder_whenProductQuantityInOrderItemGreaterThanInProductThenException() {
        List<AddOrderItemDTO> orderItems = new ArrayList<>();
        orderItems.add(new AddOrderItemDTO(BigInteger.ONE, 10));
        AddOrderDTO addOrderDTO = new AddOrderDTO();
        addOrderDTO.setClientId(BigInteger.ONE);
        addOrderDTO.setOrderItems(orderItems);

        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.addOrder(addOrderDTO));
    }

    @Test
    public void payForOrder_whenNullOrderIdThenException() {
        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.payForOrder(BigInteger.ONE, null));
    }

    @Test
    public void payForOrder_whenOrderWithIdDoesntExistThenException() {
        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.payForOrder(BigInteger.ONE, BigInteger.TWO));
    }

    @Test
    public void payForOrder_whenNullClientIdThenException() {
        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.payForOrder(null, BigInteger.ONE));
    }

    @Test
    public void payForOrder_whenPayingClientIdAndOrderClientIdNotEqualThenException() {
        Assertions.assertThrows(AuthorizationException.class,
                () -> orderService.payForOrder(BigInteger.TWO, BigInteger.ONE));
    }

    @Test
    public void payForOrder_whenOrderAlreadyPaidThenException() {
        Assertions.assertThrows(InvalidParameterException.class,
                () -> orderService.payForOrder(BigInteger.ONE, BigInteger.ONE));
    }
}
