package com.example.backend.domain.post_like.repository;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post_like.entity.PostLike;
import com.example.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
}
