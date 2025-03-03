package com.example.backend.domain.post_scrap.repository;

import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post_scrap.entity.PostScrap;
import com.example.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post post, User user);
}
