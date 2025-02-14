package com.example.backend.domain.category;

import com.example.backend.domain.post.PostListResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CategoryController {
    public final CategorySevice categoryService;

    public CategoryController(CategorySevice categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/category/{categoryId}", produces = "application/json; charset=utf-8")
    public String getCategoryNameById(@PathVariable("categoryId") Long categoryId) {
        return categoryService.getCategoryNameById(categoryId);
    }

    @GetMapping("/category/recent-posts")
    public ResponseEntity<List<PostListResponse>> getPostList(@RequestParam(value = "categoryIds") List<Long> categoryIds) {
        List<PostListResponse> postList = categoryService.getTop2PostsByCategoryId(categoryIds);
        return ResponseEntity.ok(postList);
    }

    @GetMapping("/category/recent-posts/best")
    public ResponseEntity<List<PostListResponse>> getPostList() {
        List<PostListResponse> postList = categoryService.getBestPosts();
        return ResponseEntity.ok(postList);
    }
}
