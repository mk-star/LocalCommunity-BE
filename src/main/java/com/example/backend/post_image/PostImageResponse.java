package com.example.backend.post_image;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostImageResponse {
    private String url;

    public PostImageResponse(String url) {
        this.url = url;
    }

    public PostImageResponse(PostImage postImage) {
        this.url = postImage.getUrl();
    }
}