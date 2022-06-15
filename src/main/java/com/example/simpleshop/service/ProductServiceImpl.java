package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddProductDTO;
import com.example.simpleshop.domain.model.Product;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.repository.ProductRepository;
import com.example.simpleshop.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public Product addProduct(AddProductDTO addProductDTO) {
        String productName = addProductDTO.getProductName();
        if(StringUtils.isNullOrEmpty(productName)) {
            throw new InvalidParameterException("Product name must not be null or empty");
        }
        productName = productName.trim();
        if(productRepository.findProductByName(productName).isPresent()) {
            throw new InvalidParameterException("Product with provided name already exists");
        }

        final BigDecimal productPrice = addProductDTO.getProductPrice();
        if(productPrice == null) {
            throw new InvalidParameterException("Product price must not be null");
        }
        if(productPrice.compareTo(BigDecimal.ZERO) < 1) {
            throw new InvalidParameterException("Product price must be greater than 0");
        }

        final Integer productQuantity = addProductDTO.getProductQuantity();
        if(productQuantity == null) {
            throw new InvalidParameterException("Product quantity must not be null");
        }
        if(productQuantity.compareTo(0) < 0) {
            throw new InvalidParameterException("Product quantity must be greater or equal to 0");
        }

        final Product productToAdd = new Product();
        productToAdd.setProductName(productName);
        productToAdd.setProductPrice(productPrice);
        productToAdd.setProductQuantity(productQuantity);
        return productRepository.save(productToAdd);
    }
}
