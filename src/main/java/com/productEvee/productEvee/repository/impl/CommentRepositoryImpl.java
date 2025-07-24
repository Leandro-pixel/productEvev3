package com.productEvee.productEvee.repository.impl;

import com.google.firebase.database.*;
import com.productEvee.productEvee.entity.Comment;
import com.productEvee.productEvee.repository.CommentRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private final DatabaseReference databaseReference;
    private static final String NODE_NAME = "comments";

    public CommentRepositoryImpl(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(NODE_NAME);
    }

    @Override
    public Comment save(Comment comment) {
        try {
            String id;
            if (comment.getId() != null && !comment.getId().toString().isEmpty()) {
                id = comment.getId().toString();
            } else {
                id = databaseReference.push().getKey();
                UUID uuid = UUID.fromString(id);
                comment.setUserId(uuid);
            }
            CompletableFuture<Void> future = new CompletableFuture<>();
            databaseReference.child(id).setValue(comment, (error, ref) -> {
                if (error == null) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            future.get();
            return comment;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Comment findById(String id) {
        try {
            CompletableFuture<Comment> future = new CompletableFuture<>();
            databaseReference.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    Comment comment = snapshot.getValue(Comment.class);
                    if (comment != null) {
                        UUID uuid = UUID.fromString(snapshot.getKey());
                        comment.setUserId(uuid);
                    }
                    future.complete(comment);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            CompletableFuture<Void> future = new CompletableFuture<>();
            databaseReference.child(id).removeValue((error, ref) -> {
                if (error == null) {
                    future.complete(null);
                } else {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Comment> findAll() {
        try {
            CompletableFuture<List<Comment>> future = new CompletableFuture<>();
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    List<Comment> comments = new ArrayList<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        Comment comment = child.getValue(Comment.class);
                        if (comment != null) {
                            UUID uuid = UUID.fromString(child.getKey());
                            comment.setUserId(uuid);
                            comments.add(comment);
                        }
                    }
                    future.complete(comments);
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    future.completeExceptionally(new RuntimeException(error.getMessage()));
                }
            });
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
