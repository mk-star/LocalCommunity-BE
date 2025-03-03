package com.example.backend.domain.post_like.converter;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post_like.entity.PostLike;
import com.example.backend.domain.user.User;

public class PostLikeConverter {
    public static PostLike toPostLike(Post post, User user) {
        return PostLike.builder()
                .post(post)
                .user(user)
                .build();
    }
}
