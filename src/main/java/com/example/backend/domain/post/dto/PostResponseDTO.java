package com.example.backend.domain.post.dto;

import com.example.backend.domain.user.dto.UserResponseDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class PostResponseDTO {
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreatePostResponseDTO {
        private Long postId;
        private LocalDateTime createdAt;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreViewDTO {
        private Long postId;
        private UserResponseDTO.UserInfoResponseDTO userInfo;
        private Long categoryId;
        private String title;
        private String content;
        private Integer likeCount;
        private Integer scrapCount;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<String> postImages;
        private Integer views;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostPreViewListDTO {
        private List<PostPreViewDTO> postList;
        private Integer listSize;
        private Integer totalPage;
        private Long totalElements;
        private Boolean isFirst;
        private Boolean isLast;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdatePostResponseDTO {
        private Long postId;
        private LocalDateTime updatedAt;
    }
}
