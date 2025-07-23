package com.productEvee.productEvee.repository.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.productEvee.productEvee.entity.Product;
import com.productEvee.productEvee.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static final String COLLECTION_NAME = "products";

    @Autowired
    private Firestore firestore;

    @Override
    public List<Product> findByUserId(UUID userId) {
        try {
            CollectionReference products = firestore.collection(COLLECTION_NAME);
            Query query = products.whereEqualTo("userId", userId.toString());
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            return querySnapshot.get().getDocuments().stream()
                    .map(doc -> {
                        Product product = doc.toObject(Product.class);
                        String idString = doc.getId();
                        UUID id = UUID.fromString(idString);
                        product.setUserId(id); // id do documento Firestore
                        return product;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public List<Product> findByGlobalStockTrue() {
        try {
            CollectionReference products = firestore.collection(COLLECTION_NAME);
            Query query = products.whereEqualTo("globalStock", true);
            ApiFuture<QuerySnapshot> querySnapshot = query.get();

            return querySnapshot.get().getDocuments().stream()
                    .map(doc -> {
                        Product product = doc.toObject(Product.class);
                        String idString = doc.getId();
                        UUID id = UUID.fromString(idString);
                        product.setUserId(id);
                        return product;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    @Override
    public void save(Product product) {
        try {
            DocumentReference docRef;
            if (product.getId() != null && !product.getId().toString().isEmpty()) {
                // Atualiza documento existente
                docRef = firestore.collection(COLLECTION_NAME).document(String.valueOf(product.getId()));
            } else {
                // Cria novo documento com ID gerado pelo Firestore
                docRef = firestore.collection(COLLECTION_NAME).document();
                String idString = docRef.getId();
                UUID id = UUID.fromString(idString);
                product.setUserId(id);
            }
            ApiFuture<WriteResult> result = docRef.set(product);
            result.get(); // aguardar a operação completar
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                Product product = document.toObject(Product.class);
                String idString = document.getId();
                UUID ids = UUID.fromString(idString);
                product.setUserId(ids);
                return product;
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<WriteResult> writeResult = docRef.delete();
            writeResult.get(); // aguardar a operação completar
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Product> findAll() {
        try {
            CollectionReference products = firestore.collection(COLLECTION_NAME);
            ApiFuture<QuerySnapshot> querySnapshot = products.get();

            return querySnapshot.get().getDocuments().stream()
                    .map(doc -> {
                        Product product = doc.toObject(Product.class);
                        String idString = doc.getId();
                        UUID id = UUID.fromString(idString);
                        product.setUserId(id);
                        return product;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
