package com.productEvee.productEvee.dto;

import java.util.UUID;

public class FollowRequest {
    private UUID followerId;
    private UUID followedId;

    public UUID getFollowerId() {
        return followerId;
    }

    public void setFollowerId(UUID followerId) {
        this.followerId = followerId;
    }

    public UUID getFollowedId() {
        return followedId;
    }

    public void setFollowedId(UUID followedId) {
        this.followedId = followedId;
    }
}

