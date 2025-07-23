package com.productEvee.productEvee.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.database.*;
import com.productEvee.productEvee.controller.CreateUserDto;
import com.productEvee.productEvee.controller.UpdateUserDto;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.utils.FirebaseUtils;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
public class UserService {

    private final DatabaseReference dbRef;

    public UserService() {
        this.dbRef = FirebaseDatabase.getInstance().getReference("users");
    }

    public CompletableFuture<User> createUser(CreateUserDto dto) {
    UUID userId = UUID.randomUUID();

    User user = new User(
        userId,
        dto.username(),
        dto.email(),
        dto.onesignalId(),
        Instant.now(),
        null
    );

    ApiFuture<Void> future = dbRef.child(userId.toString()).setValueAsync(user);
    CompletableFuture<User> completable = new CompletableFuture<>();

    Executors.newSingleThreadExecutor().submit(() -> {
        try {
            future.get(); // espera a operação completar
            completable.complete(user); // retorna o user
        } catch (Exception e) {
            completable.completeExceptionally(e);
        }
    });

    return completable;
}



    public CompletableFuture<Optional<User>> getUserById(String userId) {
        CompletableFuture<Optional<User>> future = new CompletableFuture<>();

        dbRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                future.complete(Optional.ofNullable(user));
            }

            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });

        return future;
    }

    public CompletableFuture<List<User>> listUsers() {
        CompletableFuture<List<User>> future = new CompletableFuture<>();

        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    User user = child.getValue(User.class);
                    if (user != null) users.add(user);
                }
                future.complete(users);
            }

            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });

        return future;
    }

    public CompletableFuture<List<User>> searchUsersByUsername(String username) {
    CompletableFuture<List<User>> future = new CompletableFuture<>();

    Query query = dbRef.orderByChild("username").startAt(username).endAt(username + "\uf8ff");

    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot snapshot) {
            List<User> matchedUsers = new ArrayList<>();
            for (DataSnapshot child : snapshot.getChildren()) {
                User user = child.getValue(User.class);
                if (user != null) {
                    matchedUsers.add(user);
                }
            }
            future.complete(matchedUsers);
        }

        @Override
        public void onCancelled(DatabaseError error) {
            future.completeExceptionally(new RuntimeException(error.getMessage()));
        }
    });

    return future;
}



    public CompletableFuture<Void> updateUserById(String userId, UpdateUserDto dto) {
        Map<String, Object> updates = new HashMap<>();

        if (dto.username() != null) {
            updates.put("username", dto.username());
        }

        // você pode adicionar outros campos aqui...

        updates.put("updateTimestamp", Instant.now().toString());

    ApiFuture<Void> future = dbRef.child(userId).updateChildrenAsync(updates);
    return FirebaseUtils.convertToCompletable(future);    }

    public CompletableFuture<Void> deleteById(String userId) {
    ApiFuture<Void> future = dbRef.child(userId).removeValueAsync();
    return FirebaseUtils.convertToCompletable(future);    }

    public CompletableFuture<Optional<User>> authenticateUser(String email) {
        CompletableFuture<Optional<User>> future = new CompletableFuture<>();

        dbRef.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                public void onDataChange(DataSnapshot snapshot) {
                    for (DataSnapshot child : snapshot.getChildren()) {
                        User user = child.getValue(User.class);
                        if (user != null) {
                            future.complete(Optional.of(user));
                            return;
                        }
                    }
                    future.complete(Optional.empty());
                }

                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });

        return future;
    }
}


