package com.example.backend.domain.post_scrap.converter;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post_scrap.entity.PostScrap;
import com.example.backend.domain.user.User;

public class PostScrapConverter {
    public static PostScrap toPostScrap(Post post, User user) {
        return PostScrap.builder()
                .post(post)
                .user(user)
                .build();
    }
}
