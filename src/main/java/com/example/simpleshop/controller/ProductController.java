package com.example.simpleshop.controller;

import com.example.simpleshop.domain.dto.AddProductDTO;
import com.example.simpleshop.domain.dto.ErrorResponse;
import com.example.simpleshop.domain.model.Product;
import com.example.simpleshop.exceptions.InvalidParameterException;
import com.example.simpleshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goods")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping("")
    public Iterable<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping("")
    public ResponseEntity<Object> addNewProduct(@RequestBody AddProductDTO addProductDTO) {
        try {
            final Product addedProduct = productService.addProduct(addProductDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedProduct);
        } catch(InvalidParameterException ex) {
            final ErrorResponse errorResponse = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
}
