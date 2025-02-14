package com.example.backend.domain.post_like;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostLikeDto {
    private Long postId;
    private String postTitle;
    private String postContent;
}
