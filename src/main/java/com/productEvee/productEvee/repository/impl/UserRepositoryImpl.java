package com.productEvee.productEvee.repository.impl;

import com.google.firebase.database.*;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final DatabaseReference databaseReference;
    private static final String NODE_NAME = "users";

    public UserRepositoryImpl(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(NODE_NAME);
    }

    @Override
    public Optional<User> findById(String id) {
        try {
            CompletableFuture<User> future = new CompletableFuture<>();
            databaseReference.child(id.toString())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user != null) {
                                user.setUserId(id);
                            }
                            future.complete(user);
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            future.completeExceptionally(new RuntimeException(error.getMessage()));
                        }
                    });
            User user = future.get();
            return Optional.ofNullable(user);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public CompletableFuture<Optional<User>> findByEmail(String email) {
        CompletableFuture<Optional<User>> future = new CompletableFuture<>();

        databaseReference.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                User user = child.getValue(User.class);
                                if (user != null) {
                                    user.setUserId(child.getKey());
                                    future.complete(Optional.of(user));
                                    return;
                                }
                            }
                        }
                        future.complete(Optional.empty());
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        future.completeExceptionally(new RuntimeException(error.getMessage()));
                    }
                });

        return future;
    }

    @Override
    public List<User> findByUsernameContainingIgnoreCase(String username) {
        List<User> result = new ArrayList<>();
        try {
            CompletableFuture<List<User>> future = new CompletableFuture<>();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null && user.getUsername() != null
                            && user.getUsername().toLowerCase().contains(username.toLowerCase())) {
                            user.setUserId(child.getKey());
                            result.add(user);
                        }
                    }
                    future.complete(result);
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
    public void save(User user) {
        try {
            String id = user.getUserId() == null ? UUID.randomUUID().toString() : user.getUserId().toString();
            user.setUserId(id); // garantir id
            CompletableFuture<Void> future = new CompletableFuture<>();
            databaseReference.child(id).setValue(user, (error, ref) -> {
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
}
