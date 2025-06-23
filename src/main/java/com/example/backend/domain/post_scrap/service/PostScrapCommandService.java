package com.example.backend.domain.post_scrap.service;

public interface PostScrapCommandService {
    void scrapPost(Long postId, Long userId);
}
