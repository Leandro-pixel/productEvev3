package com.productEvee.productEvee.controller;

import com.productEvee.productEvee.entity.Product;
import com.productEvee.productEvee.service.ProductService;

import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody ProductDto dto) {
        try {
            Product created = service.create(dto);
            return ResponseEntity.ok(created);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        } catch (ExecutionException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
public ResponseEntity<Product> update(@PathVariable String id, @RequestBody ProductDto dto) {
    try {
        Product updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return ResponseEntity.status(500).build();
    } catch (ExecutionException e) {
        return ResponseEntity.status(500).build();
    } catch (NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}


@GetMapping("/{id}")
public ResponseEntity<Product> getById(@PathVariable String id) {
    try {
        Product product = service.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(product);
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return ResponseEntity.status(500).build();
    } catch (ExecutionException e) {
        return ResponseEntity.status(500).build();
    }
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        } catch (ExecutionException e) {
            return ResponseEntity.status(500).build();
        }
    }

   @GetMapping
public ResponseEntity<ProductListResponseDTO> listByUserId(@RequestParam UUID userId) {
    try {
        return ResponseEntity.ok(service.listByUserId(userId));
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return ResponseEntity.status(500).build();
    }
}

    @GetMapping("/global")
public ResponseEntity<ProductListResponseDTO> listGlobalProducts() {
    try {
        return ResponseEntity.ok(service.listAll());
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        return ResponseEntity.status(500).build();
    } 
}


    @PostMapping("/copy-to-personal/{id}")
    public ResponseEntity<Product> copyGlobalToPersonal(@PathVariable String id, @RequestParam UUID userId) {
        try {
            return ResponseEntity.ok(service.copyGlobalProductToUser(id, userId));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        } 
    }

    @PostMapping("/comments/{id}")
    public ResponseEntity<Product> addComment(@PathVariable String id, @RequestBody CommentDto dto) {
        try {
            return ResponseEntity.ok(service.addComment(id, dto));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(500).build();
        } catch (ExecutionException e) {
            return ResponseEntity.status(500).build();
        }
    }
}
