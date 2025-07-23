package com.productEvee.productEvee.repository;

import com.productEvee.productEvee.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findByUsernameContainingIgnoreCase(String username);
    void save(User user);
}
