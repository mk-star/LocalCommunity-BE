package com.example.backend.domain.post_image.converter;

import com.example.backend.domain.post_image.dto.PostImageResponseDTO;
import com.example.backend.domain.post_image.entity.PostImage;

import java.util.List;

public class PostImageConverter {
    public static PostImage toPostImage(String fileUrl) {
        return PostImage.builder()
                .fileUrl(fileUrl)
                .build();
    }

    public static List<PostImageResponseDTO.PostImageDTO> toUploadImageResultDTO(List<PostImage> postImages) {
        return postImages.stream()
                .map(postImage -> PostImageResponseDTO.PostImageDTO.builder()
                        .id(postImage.getId())
                        .fileUrl(postImage.getFileUrl())
                        .build())
                .toList();
    }
}
