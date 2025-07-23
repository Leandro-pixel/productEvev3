package com.productEvee.productEvee.repository;

import com.productEvee.productEvee.entity.Product;
import java.util.List;
import java.util.UUID;

public interface ProductRepository {
    List<Product> findByUserId(UUID userId);
    List<Product> findByGlobalStockTrue();
    void save(Product product);
    Product findById(String id);
    void deleteById(String id);
    List<Product> findAll();
}
