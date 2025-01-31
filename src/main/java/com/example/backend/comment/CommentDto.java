package com.example.backend.comment;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
public class CommentDto {

    private Long id;
    private String content;
    private LocalDateTime createdDate;
    private String userName;

    public CommentDto(Long id, String content, LocalDateTime createdDate, String userName) {
        this.id = id;
        this.content = content;
        this.createdDate = createdDate;
        this.userName = userName;
    }


}
