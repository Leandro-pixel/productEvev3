package com.productEvee.productEvee.repository.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.productEvee.productEvee.entity.Follow;
import com.productEvee.productEvee.entity.User;
import com.productEvee.productEvee.repository.FollowRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
public class FollowRepositoryImpl implements FollowRepository {

    private static final String COLLECTION_NAME = "follows";

    @Autowired
    private Firestore firestore;

    @Override
    public void save(Follow follow) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(follow.getId().toString());
        ApiFuture<WriteResult> future = docRef.set(follow);
        future.get();
    }

    @Override
    public Follow findById(UUID id) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id.toString());
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(Follow.class);
        } else {
            return null;
        }
    }

    @Override
    public List<Follow> findByFollower(User follower) throws ExecutionException, InterruptedException {
        CollectionReference follows = firestore.collection(COLLECTION_NAME);
        Query query = follows.whereEqualTo("follower.id", follower.getUserId().toString());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Follow> results = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            results.add(doc.toObject(Follow.class));
        }
        return results;
    }

    @Override
    public List<Follow> findByFollowed(User followed) throws ExecutionException, InterruptedException {
        CollectionReference follows = firestore.collection(COLLECTION_NAME);
        Query query = follows.whereEqualTo("followed.id", followed.getUserId().toString());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Follow> results = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            results.add(doc.toObject(Follow.class));
        }
        return results;
    }

    @Override
    public boolean existsByFollowerAndFollowed(User follower, User followed) throws ExecutionException, InterruptedException {
        CollectionReference follows = firestore.collection(COLLECTION_NAME);
        Query query = follows
                .whereEqualTo("follower.id", follower.getUserId().toString())
                .whereEqualTo("followed.id", followed.getUserId().toString());

        ApiFuture<QuerySnapshot> querySnapshot = query.get();
        return !querySnapshot.get().isEmpty();
    }

    @Override
    public List<Follow> findByFollowedUserId(UUID followedUserId) throws ExecutionException, InterruptedException {
        CollectionReference follows = firestore.collection(COLLECTION_NAME);
        Query query = follows.whereEqualTo("followed.id", followedUserId.toString());
        ApiFuture<QuerySnapshot> querySnapshot = query.get();

        List<Follow> results = new ArrayList<>();
        for (DocumentSnapshot doc : querySnapshot.get().getDocuments()) {
            results.add(doc.toObject(Follow.class));
        }
        return results;
    }
}
