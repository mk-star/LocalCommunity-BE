package com.example.backend.domain.post_like.service;

public interface PostLikeCommandService {
    void likePost(Long postId, Long userId);
}
