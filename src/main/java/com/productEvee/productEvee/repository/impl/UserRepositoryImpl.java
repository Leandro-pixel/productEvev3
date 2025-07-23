package com.productEvee.productEvee.repository.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Firestore firestore;
    private static final String COLLECTION_NAME = "users";

    public UserRepositoryImpl(Firestore firestore) {
        this.firestore = firestore;
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id.toString());
            DocumentSnapshot doc = docRef.get().get();
            if (doc.exists()) {
                User user = doc.toObject(User.class);
                if (user != null) user.setUserId(id);
                return Optional.ofNullable(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME)
                    .whereEqualTo("email", email)
                    .get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            if (!docs.isEmpty()) {
                User user = docs.get(0).toObject(User.class);
                String idString = docs.get(0).getId();
                UUID id = UUID.fromString(idString);
                user.setUserId(id);
                return Optional.of(user);
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByUsernameContainingIgnoreCase(String username) {
        List<User> result = new ArrayList<>();
        try {
            ApiFuture<QuerySnapshot> future = firestore.collection(COLLECTION_NAME).get();
            List<QueryDocumentSnapshot> docs = future.get().getDocuments();
            for (QueryDocumentSnapshot doc : docs) {
                User user = doc.toObject(User.class);
                if (user.getUsername().toLowerCase().contains(username.toLowerCase())) {
                    UUID id = UUID.fromString(doc.getId());
                    user.setUserId(id);
                    result.add(user);
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void save(User user) {
        try {
            String id = user.getUserId() == null ? UUID.randomUUID().toString() : user.getUserId().toString();
            firestore.collection(COLLECTION_NAME).document(id).set(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
