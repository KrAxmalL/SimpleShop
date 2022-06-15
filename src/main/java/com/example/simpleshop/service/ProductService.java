package com.example.simpleshop.service;

import com.example.simpleshop.domain.dto.AddProductDTO;
import com.example.simpleshop.domain.model.Product;

public interface ProductService {

    Iterable<Product> getAllProducts();

    Product addProduct(AddProductDTO addProductDTO);
}
