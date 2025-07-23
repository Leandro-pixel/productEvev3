package com.productEvee.productEvee.controller;

import com.productEvee.productEvee.entity.Product;

import java.util.List;

public class ProductListResponseDTO {
    private long total;
    private List<Product> products;

    public ProductListResponseDTO(long total, List<Product> products) {
        this.total = total;
        this.products = products;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
