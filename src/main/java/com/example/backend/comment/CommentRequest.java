package com.example.backend.comment;

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
