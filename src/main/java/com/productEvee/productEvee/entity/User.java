package com.productEvee.productEvee.entity;

import jakarta.persistence.Column;

public class User {

    private String userId;
    private String username;
    private String email;
    private String onesignalId;

    @Column(name = "creationTimestamp")
    private long creationTimestamp;


    public User() {}

    public User(String userId, String username, String email, String onesignalId, long creationTimestamp) {
    this.userId = userId;
    this.username = username;
    this.email = email;
    this.onesignalId = onesignalId;
    this.creationTimestamp = creationTimestamp;
}

public long getCreationTimestamp() {
    return creationTimestamp;
}

public void setCreationTimestamp(long creationTimestamp) {
    this.creationTimestamp = creationTimestamp;
}


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOnesignalId() {
        return onesignalId;
    }

    public void setOnesignalId(String onesignalId) {
        this.onesignalId = onesignalId;
    }
}
