package com.productEvee.productEvee.repository;

import com.productEvee.productEvee.entity.Follow;
import com.productEvee.productEvee.entity.User;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public interface FollowRepository {
    void save(Follow follow) throws ExecutionException, InterruptedException;
    Follow findById(UUID id) throws ExecutionException, InterruptedException;
    List<Follow> findByFollower(User follower) throws ExecutionException, InterruptedException;
    List<Follow> findByFollowed(User followed) throws ExecutionException, InterruptedException;
    boolean existsByFollowerAndFollowed(User follower, User followed) throws ExecutionException, InterruptedException;
    List<Follow> findByFollowedUserId(String followedUserId) throws ExecutionException, InterruptedException;
}
