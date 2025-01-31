package com.example.backend.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostListResponse {
    private Long postId;
    private String nickname;
    private String title;
    private String content;
    private String category;
    private int likeCount;
    private int commentCount;

    public PostListResponse(Long postId, String title, String nickname, String content, String category, int likeCount, int commentCount) {
        this.postId = postId;
        this.title = title;
        this.nickname = nickname;
        this.content = content;
        this.category = category;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
    }

    public static PostListResponse toDto(Post post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getUser().getNickname(),
                post.getContent(),
                post.getCategory().getName(),
                post.getLikeCount(),
                post.getCommentCount()
        );
    }
}
