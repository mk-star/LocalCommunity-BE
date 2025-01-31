package com.example.backend.category;

import com.example.backend.comment.Comment;
import com.example.backend.comment.CommentResponse;
import com.example.backend.post.Post;
import com.example.backend.post.PostListResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CategorySevice {
    public final CategoryRepository categoryRepository;

    public CategorySevice(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public String getCategoryNameById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new NoSuchElementException("Invalid category ID"));
        return category.getName();
    }

    @Transactional(readOnly = true)
    public List<PostListResponse> getTop2PostsByCategoryId(List<Long> categoryIds) {
        List<PostListResponse> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 2);

        for (Long categoryId : categoryIds) {
            List<Post> posts = categoryRepository.findByCategoryId(categoryId, pageable);
            addPostsToResult(result, posts);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public List<PostListResponse> getBestPosts() {
        List<PostListResponse> result = new ArrayList<>();
        Pageable pageable = PageRequest.of(0, 2);

        // 각 조건에 따라 게시물을 추가하는 메서드 호출
        getPostsByView(result, pageable, 1);
        getPostsByLikeCount(result, pageable, 0);
        getPostsByCommentCount(result, pageable, 0);

        return result;
    }

    private void getPostsByView(List<PostListResponse> result, Pageable pageable, int minView) {
        List<Post> posts = categoryRepository.findTop2ByView(minView, pageable);
        addPostsToResult(result, posts);
    }

    private void getPostsByLikeCount(List<PostListResponse> result, Pageable pageable, int minLikeCount) {
        List<Post> posts = categoryRepository.findTop2ByLikeCount(minLikeCount, pageable);
        addPostsToResult(result, posts);
    }

    private void getPostsByCommentCount(List<PostListResponse> result, Pageable pageable, int minCommentCount) {
        List<Post> posts = categoryRepository.findTop2ByCommentCount(minCommentCount, pageable);
        addPostsToResult(result, posts);
    }

    private void addPostsToResult(List<PostListResponse> result, List<Post> posts) {
        if (posts.isEmpty()) {
            for (int i = 0; i < 2; i++) {
                result.add(null);
            }
        } else {
            result.addAll(posts.stream()
                    .map(PostListResponse::toDto)
                    .collect(Collectors.toList()));

            if (posts.size() == 1) {
                result.add(null);
            }
        }
    }

}

