package com.productEvee.productEvee.repository;

import com.productEvee.productEvee.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface UserRepository {
    CompletableFuture<Optional<User>> findByEmail(String email);
    Optional<User> findById(String id);
    List<User> findByUsernameContainingIgnoreCase(String username);
    void save(User user);
}
