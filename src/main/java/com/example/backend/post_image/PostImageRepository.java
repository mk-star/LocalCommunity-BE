package com.example.backend.post_image;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findByPostId(Long postId);
    Optional<PostImage> findByUrl(String url);
}