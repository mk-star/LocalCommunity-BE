package com.example.backend.post;

import com.example.backend.post_image.PostImage;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostEditRequest {
    private String title;
    private String content;
    private List<PostImage> postImages;
    private List<String> currentImageUrls;
    @JsonProperty("isEdited")
    private boolean isEdited;
    @JsonProperty("isScrapped")
    private boolean isScrapped;
    @JsonProperty("isLiked")
    private boolean isLiked;
    private Long categoryId;
}
