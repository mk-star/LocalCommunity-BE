package com.example.backend.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentEditRequest {
    private String content;
    @JsonProperty("isEdited")
    private boolean isEdited;

    public CommentEditRequest(String content, boolean isEdited) {
        this.content = content;
        this.isEdited = isEdited;
    }
}
