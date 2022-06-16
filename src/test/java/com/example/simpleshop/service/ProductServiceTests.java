package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddProductDTO;
import com.example.simpleshop.domain.model.Product;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @TestConfiguration
    static class ProductServiceTestsConfiguration {

        @Bean
        public ProductRepository productRepository() {
            ProductRepository productRepository = Mockito.mock(ProductRepository.class);
            Mockito.when(productRepository.findProductByName("Name"))
                    .thenReturn(Optional.of(new Product(null, "Name", null, null)));
            return productRepository;
        }

        @Bean
        public ProductService productService() {
            return new ProductServiceImpl(productRepository());
        }
    }

    @Autowired
    private ProductService productService;

    @Test
    public void addProduct_whenNullProductThenException() {
        Assertions.assertThrows(InvalidParameterException.class,
                                () -> productService.addProduct(null));
    }

    @Test
    public void addProduct_whenNullProductNameThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName(null);
        addProductDTO.setProductPrice(BigDecimal.ONE);
        addProductDTO.setProductQuantity(1);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }

    @Test
    public void addProduct_whenEmptyProductNameThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName("        ");
        addProductDTO.setProductPrice(BigDecimal.ONE);
        addProductDTO.setProductQuantity(1);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }

    @Test
    public void addProduct_whenProductNameNotUniqueThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName("Name");
        addProductDTO.setProductPrice(BigDecimal.ONE);
        addProductDTO.setProductQuantity(1);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }

    @Test
    public void addProduct_whenNullProductPriceThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName("Name");
        addProductDTO.setProductPrice(null);
        addProductDTO.setProductQuantity(1);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }

    @Test
    public void addProduct_whenLessEqualZeroProductPriceThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName("Name");
        addProductDTO.setProductPrice(BigDecimal.ZERO);
        addProductDTO.setProductQuantity(1);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }

    @Test
    public void addProduct_whenNullProductQuantityThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName("Name");
        addProductDTO.setProductPrice(BigDecimal.ONE);
        addProductDTO.setProductQuantity(null);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }

    @Test
    public void addProduct_whenLessThanZeroProductQuantityThenException() {
        AddProductDTO addProductDTO = new AddProductDTO();
        addProductDTO.setProductName("Name");
        addProductDTO.setProductPrice(BigDecimal.ONE);
        addProductDTO.setProductQuantity(-1);
        Assertions.assertThrows(InvalidParameterException.class,
                () -> productService.addProduct(addProductDTO));
    }
}
