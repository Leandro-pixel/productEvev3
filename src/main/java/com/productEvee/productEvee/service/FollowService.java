package com.productEvee.productEvee.service;

import com.productEvee.productEvee.entity.Follow;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.repository.FollowRepository;
import com.productEvee.productEvee.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    public FollowService(FollowRepository followRepository, UserRepository userRepository) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
    }

    public boolean followUser(UUID followerId, UUID followedId) throws ExecutionException, InterruptedException {
        Optional<User> followerOpt = userRepository.findById(followerId);
        Optional<User> followedOpt = userRepository.findById(followedId);

        if (followerOpt.isEmpty() || followedOpt.isEmpty()) return false;

        User follower = followerOpt.get();
        User followed = followedOpt.get();

        if (followRepository.existsByFollowerAndFollowed(follower, followed)) return false;

        Follow follow = new Follow(follower, followed);
        followRepository.save(follow);
        return true;
    }

    public List<User> getFollowers(UUID userId) throws ExecutionException, InterruptedException {
        List<Follow> follows = followRepository.findByFollowedUserId(userId);
        return follows.stream()
            .map(Follow::getFollower)
            .collect(Collectors.toList());
    }
}
