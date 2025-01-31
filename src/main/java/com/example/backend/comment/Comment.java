package com.example.backend.comment;

import com.example.backend.comment_like.CommentLike;
import com.example.backend.post.Post;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@DynamicUpdate
@EntityListeners(AuditingEntityListener.class)
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    @Column(nullable = false)
    private String content;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parentComment;
    @OneToMany(mappedBy = "parentComment", orphanRemoval = true)
    private List<Comment> childrenComment = new ArrayList<>();
    @OneToMany(mappedBy = "comment", orphanRemoval = true)
    private List<CommentLike> commentLikes = new ArrayList<>();
    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    @Column(name = "is_edited")
    private boolean isEdited = false;
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    public Comment(User user, Post post, String content, Comment parentComment) {
        this.user = user;
        this.post = post;
        this.content = content;
        this.parentComment = parentComment;
    }
    public void update(String content, boolean isEdited) {
        this.content = content;
        this.isEdited = isEdited;
    }
    public void addChildrenComment(Comment childComment) {
        this.childrenComment.add(childComment);
        childComment.setParentComment(this);
    }
}
