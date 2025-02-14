package com.example.backend.domain.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long userId;
    private Long postId;
    private String content;
    private Long parentId;
}
