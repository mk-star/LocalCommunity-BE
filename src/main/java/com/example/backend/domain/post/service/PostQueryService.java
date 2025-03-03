package com.example.backend.domain.post.service;

import com.example.backend.domain.post.dto.PostResponseDTO;
import com.example.backend.domain.post.entity.Post;
import org.springframework.data.domain.Page;

public interface PostQueryService {
    PostResponseDTO.PostPreViewDTO getPost(Long postId, Long userId);
    Page<Post> getPostList(Long categoryId, String keyword, Integer page);
}
