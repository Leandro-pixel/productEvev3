package com.productEvee.productEvee.repository.impl;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.productEvee.productEvee.entity.Comment;
import com.productEvee.productEvee.repository.CommentRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class CommentRepositoryImpl implements CommentRepository {

    private static final String COLLECTION_NAME = "comments";

    @Autowired
    private Firestore firestore;

    @Override
    public Comment save(Comment comment) {
        try {
            DocumentReference docRef;
            if (comment.getId() != null && !comment.getId().toString().isEmpty()) {
                docRef = firestore.collection(COLLECTION_NAME).document(String.valueOf(comment.getId()));
            } else {
                docRef = firestore.collection(COLLECTION_NAME).document(); // gera ID automático
                String idString = docRef.getId();
                UUID id = UUID.fromString(idString);
                comment.setUserId(id);
            }
            ApiFuture<WriteResult> writeResult = docRef.set(comment);
            writeResult.get();  // esperar confirmação de escrita
            return comment;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Comment findById(String id) {
        try {
            DocumentReference docRef = firestore.collection(COLLECTION_NAME).document(id);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            DocumentSnapshot document = future.get();
            if (document.exists()) {
                return document.toObject(Comment.class);
            } else {
                return null;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            ApiFuture<WriteResult> writeResult = firestore.collection(COLLECTION_NAME).document(id).delete();
            writeResult.get();  // esperar confirmação
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Comment> findAll() {
        try {
            ApiFuture<QuerySnapshot> querySnapshot = firestore.collection(COLLECTION_NAME).get();
            return querySnapshot.get().getDocuments().stream()
                    .map(doc -> {
                        Comment comment = doc.toObject(Comment.class);
                        String idString = doc.getId();
                        UUID id = UUID.fromString(idString);
                        comment.setUserId(id); // setar ID do documento
                        return comment;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
