package com.example.backend.domain.post.service;
import com.example.backend.domain.post.dto.PostRequestDTO;
import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post_image.entity.PostImage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostCommandService {
    Post createPost(PostRequestDTO.CreatePostRequestDTO request, Long userId);
    void deletePost(Long postId);
    Post updatePost(Long postId, PostRequestDTO.UpdatePostRequestDTO request, List<MultipartFile> images);
    List<PostImage> uploadImage(List<MultipartFile> images);
}
