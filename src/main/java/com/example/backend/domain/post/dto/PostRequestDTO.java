package com.example.backend.domain.post.dto;

import lombok.Getter;

import java.util.List;

public class PostRequestDTO {
    @Getter
    public static class CreatePostRequestDTO {
        private Long categoryId;
        private String title;
        private String content;
        private List<String> fileUrls;
    }

    @Getter
    public static class UpdatePostRequestDTO {
        private String title;
        private String content;
        private List<String> fileUrls;
    }
}
