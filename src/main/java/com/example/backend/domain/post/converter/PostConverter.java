package com.example.backend.domain.post.converter;

import com.example.backend.domain.category.Category;
import com.example.backend.domain.post.dto.PostRequestDTO;
import com.example.backend.domain.post.dto.PostResponseDTO;
import com.example.backend.domain.post.entity.Post;
import com.example.backend.domain.post_image.entity.PostImage;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.converter.UserConverter;
import com.example.backend.global.util.RedisUtil;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class PostConverter {
    public static Post toPost(PostRequestDTO.CreatePostRequestDTO request, User user, Category category) {
        return Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(user)
                .category(category)
                .build();
    }
    public static PostResponseDTO.CreatePostResponseDTO toCreatePostResultDTO(Post post) {
        return PostResponseDTO.CreatePostResponseDTO.builder()
                .postId(post.getId())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static PostResponseDTO.PostPreViewDTO postPreViewDTO(Post post, Long view) {
        return PostResponseDTO.PostPreViewDTO.builder()
                .postId(post.getId())
                .userInfo(UserConverter.toUserInfo(post.getUser()))
                .categoryId(post.getCategory().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(view)
                .likeCount(post.getLikeCount())
                .scrapCount(post.getScrapCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .postImages(post.getPostImages().stream().map(PostImage::getFileUrl).toList())
                .build();
    }

    public static PostResponseDTO.PostPreViewListDTO postPreViewListDTO(Page<Post> postList, RedisUtil redisUtil) {
        List<PostResponseDTO.PostPreViewDTO> postPreViewDTOList = postList.stream()
                .map(post -> {
                    String redisKey = "post:" + post.getId();
                    Object viewObj = redisUtil.get(redisKey);
                    Long view = viewObj == null ? post.getView() : Long.parseLong(viewObj.toString());
                    return postPreViewDTO(post, view);
                })
                .collect(Collectors.toList());

        return PostResponseDTO.PostPreViewListDTO.builder()
                .isLast(postList.isLast())
                .isFirst(postList.isFirst())
                .totalPage(postList.getTotalPages())
                .totalElements(postList.getTotalElements())
                .listSize(postPreViewDTOList.size())
                .postList(postPreViewDTOList)
                .build();
    }


    public static PostResponseDTO.UpdatePostResponseDTO toUpdatePostResultDTO(Post post) {
        return PostResponseDTO.UpdatePostResponseDTO.builder()
                .postId(post.getId())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}