package com.example.backend.domain.post.entity;

import com.example.backend.domain.category.entity.Category;
import com.example.backend.domain.comment.entity.Comment;
import com.example.backend.domain.post_image.entity.PostImage;
import com.example.backend.domain.post_like.entity.PostLike;
import com.example.backend.domain.post_scrap.entity.PostScrap;
import com.example.backend.domain.user.User;
import com.example.backend.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int view;

    @Column(name = "like_count", columnDefinition = "integer default 0")
    private int likeCount;

    @Column(name = "scrap_count", columnDefinition = "integer default 0")
    private int scrapCount;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostScrap> postScraps = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostImage> postImages = new ArrayList<>();

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setView(int view) {
        this.view = view;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public void setScrapCount(int scrapCount) {
        this.scrapCount = scrapCount;
    }
}
