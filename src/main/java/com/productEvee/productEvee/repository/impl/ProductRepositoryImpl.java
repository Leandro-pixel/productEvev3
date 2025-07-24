package com.productEvee.productEvee.repository.impl;

import com.google.firebase.database.*;
import com.productEvee.productEvee.entity.Product;
import com.productEvee.productEvee.repository.ProductRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final DatabaseReference databaseReference;
    private static final String NODE_NAME = "products";

    public ProductRepositoryImpl(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(NODE_NAME);
    }

    @Override
    public List<Product> findByUserId(UUID userId) {
        try {
            CompletableFuture<List<Product>> future = new CompletableFuture<>();
            databaseReference.orderByChild("userId").equalTo(userId.toString())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Product> products = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Product product = child.getValue(Product.class);
                            if (product != null) {
                                UUID id = UUID.fromString(child.getKey());
                                product.setUserId(id);
                                products.add(product);
                            }
                        }
                        future.complete(products);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(new RuntimeException(error.getMessage()));
                    }
                });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Product> findByGlobalStockTrue() {
        try {
            CompletableFuture<List<Product>> future = new CompletableFuture<>();
            databaseReference.orderByChild("globalStock").equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Product> products = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Product product = child.getValue(Product.class);
                            if (product != null) {
                                UUID id = UUID.fromString(child.getKey());
                                product.setUserId(id);
                                products.add(product);
                            }
                        }
                        future.complete(products);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(new RuntimeException(error.getMessage()));
                    }
                });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void save(Product product) {
        try {
            String id;
            if (product.getId() != null && !product.getId().toString().isEmpty()) {
                id = product.getId().toString();
            } else {
                // Gerar novo ID no Realtime Database
                id = databaseReference.push().getKey();
                UUID uuid = UUID.fromString(id);
                product.setUserId(uuid);
            }
            CompletableFuture<Void> future = new CompletableFuture<>();
            databaseReference.child(id).setValue(product, (error, ref) -> {
                if (error == null) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product findById(String id) {
        try {
            CompletableFuture<Product> future = new CompletableFuture<>();
            databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Product product = snapshot.getValue(Product.class);
                    if (product != null) {
                        UUID uuid = UUID.fromString(snapshot.getKey());
                        product.setUserId(uuid);
                    }
                    future.complete(product);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            databaseReference.child(id).removeValue((error, ref) -> {
                if (error == null) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            CompletableFuture<List<Product>> future = new CompletableFuture<>();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Product> products = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Product product = child.getValue(Product.class);
                        if (product != null) {
                            UUID uuid = UUID.fromString(child.getKey());
                            product.setUserId(uuid);
                            products.add(product);
                        }
                    }
                    future.complete(products);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
