package com.productEvee.productEvee.repository;

import com.productEvee.productEvee.entity.Comment;

import java.util.List;

public interface CommentRepository {
    Comment save(Comment comment);
    Comment findById(String id);
    void deleteById(String id);
    List<Comment> findAll();
}
