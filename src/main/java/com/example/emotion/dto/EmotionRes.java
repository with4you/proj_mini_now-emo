package com.example.emotion.dto;

import java.time.Instant;

public class EmotionRes {
    private Long id;
    private Integer level;
    private Instant createdAt;

    public EmotionRes(Long id, Integer level, Instant createdAt) {
        this.id = id;
        this.level = level;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Integer getLevel() {
        return level;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}


