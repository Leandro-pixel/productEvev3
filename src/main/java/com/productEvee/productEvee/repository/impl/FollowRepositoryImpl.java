package com.productEvee.productEvee.repository.impl;

import com.google.firebase.database.*;
import com.productEvee.productEvee.entity.Follow;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.repository.FollowRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class FollowRepositoryImpl implements FollowRepository {

    private static final String NODE_NAME = "follows";

    private final DatabaseReference databaseReference;

    @Autowired
    public FollowRepositoryImpl(DatabaseReference databaseReference) {
        // Referência para a coleção "follows" no Realtime Database
        this.databaseReference = databaseReference.child(NODE_NAME);
    }


    @Override
    public void save(Follow follow) throws ExecutionException, InterruptedException {
        String id = follow.getId() != null ? follow.getId().toString() : UUID.randomUUID().toString();
        //follow.setFollower(UUID.fromString(id)); // Garante que o id esteja setado

        CompletableFuture<Void> future = new CompletableFuture<>();
        databaseReference.child(id).setValue(follow, (error, ref) -> {
            if (error == null) {
                future.complete(null);
            } else {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        future.get();
    }

    @Override
    public Follow findById(UUID id) throws ExecutionException, InterruptedException {
        CompletableFuture<Follow> future = new CompletableFuture<>();
        databaseReference.child(id.toString()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Follow follow = snapshot.getValue(Follow.class);
                future.complete(follow);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(new RuntimeException(error.getMessage()));
            }
        });
        return future.get();
    }

    @Override
    public List<Follow> findByFollower(User follower) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Follow>> future = new CompletableFuture<>();
        databaseReference.orderByChild("follower/id").equalTo(follower.getUserId().toString())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Follow> results = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Follow f = child.getValue(Follow.class);
                        results.add(f);
                    }
                    future.complete(results);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
        return future.get();
    }

    @Override
    public List<Follow> findByFollowed(User followed) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Follow>> future = new CompletableFuture<>();
        databaseReference.orderByChild("followed/id").equalTo(followed.getUserId().toString())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Follow> results = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Follow f = child.getValue(Follow.class);
                        results.add(f);
                    }
                    future.complete(results);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
        return future.get();
    }

    @Override
    public boolean existsByFollowerAndFollowed(User follower, User followed) throws ExecutionException, InterruptedException {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        databaseReference.orderByChild("follower/id").equalTo(follower.getUserId().toString())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    boolean exists = false;
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Follow f = child.getValue(Follow.class);
                        if (f.getFollowed() != null &&
                            followed.getUserId().toString().equals(f.getFollowed().getUserId().toString())) {
                            exists = true;
                            break;
                        }
                    }
                    future.complete(exists);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
        return future.get();
    }

    @Override
    public List<Follow> findByFollowedUserId(String followedUserId) throws ExecutionException, InterruptedException {
        CompletableFuture<List<Follow>> future = new CompletableFuture<>();
        databaseReference.orderByChild("followed/id").equalTo(followedUserId.toString())
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Follow> results = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Follow f = child.getValue(Follow.class);
                        results.add(f);
                    }
                    future.complete(results);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
        return future.get();
    }
}
