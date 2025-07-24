package com.productEvee.productEvee.service;

import com.google.api.core.ApiFuture;
import com.google.firebase.database.*;
import com.productEvee.productEvee.controller.CreateUserDto;
import com.productEvee.productEvee.controller.UpdateUserDto;
import com.productEvee.productEvee.dto.FirebaseUserDTO;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.utils.FirebaseUtils;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class UserService {

    private final DatabaseReference dbRef;

    public UserService() {
        this.dbRef = FirebaseDatabase.getInstance().getReference("users");
    }
public CompletableFuture<User> createUser(CreateUserDto dto) {
    String userId = UUID.randomUUID().toString();
    long now = System.currentTimeMillis();

    User user = new User(
        userId,
        dto.username(),
        dto.email(),
        dto.onesignalId(),
        now
        //null
    );

    ApiFuture<Void> future = dbRef.child(userId).setValueAsync(user);
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
                FirebaseUserDTO userDTO = child.getValue(FirebaseUserDTO.class);
                if (userDTO != null) {
                    User user = new User();
                    user.setEmail(userDTO.email);
                    user.setUsername(userDTO.username);
                    user.setUserId(userDTO.userId);
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


    // Adiciona timeout de segurança de 5 segundos
    CompletableFuture<Optional<User>> timeoutFuture = failAfter(Duration.ofSeconds(5));
    return future.applyToEither(timeoutFuture, user -> user);
}

// Método auxiliar para criar um future com timeout
private static <T> CompletableFuture<T> failAfter(Duration duration) {
    final CompletableFuture<T> promise = new CompletableFuture<>();
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    scheduler.schedule(() -> {
        promise.completeExceptionally(new TimeoutException("Tempo limite excedido ao consultar o Firebase"));
        scheduler.shutdown();
    }, duration.toMillis(), TimeUnit.MILLISECONDS);
    return promise;
}

}


