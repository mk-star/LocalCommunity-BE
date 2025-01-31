package com.example.backend.post;

import com.example.backend.category.Category;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostDto {
    //postTable의 id이다
    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private String userName;
    private int likeCount;
    private int commentCount;
    private String categoryName;

    public PostDto(Long id, String title, String content, LocalDateTime createdDate, LocalDateTime modifiedDate, String userName, int likeCount,int commentCount, String categoryName) {
        this.id = id;
        this.categoryName=categoryName;
        this.title = title;
        this.content = content;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.userName = userName;
        this.likeCount = likeCount;
        this.commentCount=commentCount;
    }
}
