package com.example.backend.comment;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CommentResponse {
    private Long commentId;
    private Long userId;
    private Long postId;
    private Long parentId;
    private List<CommentResponse> children;
    private String nickname;
    private String content;
    private int likeCount;
    @JsonProperty("isDeleted")
    private boolean isDeleted;
    @JsonProperty("isEdited")
    private boolean isEdited;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime  modifiedDate;
    private Long loginId;
    private String profile_url;

    public CommentResponse(Long commentId, Long userId, Long postId, Long parentId, List<CommentResponse> children, String nickname, String content, int likeCount, boolean isDeleted, boolean isEdited, LocalDateTime createdDate, LocalDateTime modifiedDate, Long loginId, String profile_url) {
        this.commentId = commentId;
        this.userId = userId;
        this.postId = postId;
        this.parentId = parentId;
        this.children = children;
        this.nickname = nickname;
        this.content = content;
        this.likeCount = likeCount;
        this.isDeleted = isDeleted;
        this.isEdited = isEdited;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.loginId = loginId;
        this.profile_url = profile_url;
    }

    public static CommentResponse toDto(Comment comment, List<CommentResponse> children, Long loginId) {
        return new CommentResponse(
                comment.getId(),
                comment.getUser().getId(),
                comment.getPost().getId(),
                comment.getParentComment() != null ? comment.getParentComment().getId() : null,
                children,
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCommentLikes().size(),
                comment.isDeleted(),
                comment.isEdited(),
                comment.getCreatedDate(),
                comment.getModifiedDate(),
                loginId,
                comment.getUser().getProfile_url()
        );
    }
}
