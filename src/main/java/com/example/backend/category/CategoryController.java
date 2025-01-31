package com.example.backend.category;

import com.example.backend.post.Post;
import com.example.backend.post.PostListResponse;
import com.example.backend.post.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
