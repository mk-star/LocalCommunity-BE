package com.example.backend.post;

import com.example.backend.category.Category;
import com.example.backend.comment.Comment;
import com.example.backend.post_image.PostImage;
import com.example.backend.post_like.PostLike;
import com.example.backend.post_scrap.PostScrap;
import com.example.backend.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
@EntityListeners(AuditingEntityListener.class)
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    private String title;
    private String content;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();
    @OneToMany(mappedBy = "post", orphanRemoval = true)
    private List<PostScrap> postScraps = new ArrayList<>();
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();
    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime createdDate;
    @LastModifiedDate
    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;
    @Column(name = "is_edited")
//    private boolean isEdited = false;
    private Boolean isEdited = false;
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;
    @Column(name = "like_count", columnDefinition = "integer default 0")
    private int likeCount;
    @Column(name = "comment_count", columnDefinition = "integer default 0")
    private int commentCount;

    public Post(String title, String content, User user, Category category) {
        this.title = title;
        this.content = content;
        this.user = user;
        this.category = category;
    }

    public void update(String title, String content, boolean isEdited) {
        this.title = title;
        this.content = content;
        this.isEdited = isEdited;
    }

    public void addPostImage(PostImage postImage) {
        this.postImages.add(postImage);
        postImage.setPost(this);
    }

    public void removePostImage(PostImage postImage) {
        this.postImages.remove(postImage);
        postImage.setPost(null);
    }
    public void increaseLikeCount() {
        this.likeCount += 1;
    }
    public void increaseCommentCount() {
        this.commentCount += 1;
    }
    public void decreaseCommentCount() {
        this.commentCount -= 1;
    }
}
