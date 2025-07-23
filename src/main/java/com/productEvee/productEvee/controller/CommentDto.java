package com.productEvee.productEvee.controller;

import java.util.UUID;

public class CommentDto {
    private UUID userId;
    private String content;

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getText() {
        return content;
    }

    public void setText(String content) {
        this.content = content;
    }
}
