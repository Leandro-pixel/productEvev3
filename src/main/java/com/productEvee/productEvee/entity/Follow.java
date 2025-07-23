package com.productEvee.productEvee.entity;


import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "follows", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"follower_id", "followed_id"})
})
public class Follow {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    public Follow() {}

    public Follow(User follower, User followed) {
        this.follower = follower;
        this.followed = followed;
    }

    public UUID getId() {
        return id;
    }

    public User getFollower() {
        return follower;
    }

    public User getFollowed() {
        return followed;
    }

    public void setFollower(User follower) {
        this.follower = follower;
    }

    public void setFollowed(User followed) {
        this.followed = followed;
    }
}

