package com.example.backend.post;

import com.example.backend.config.RedisDao;
import com.example.backend.signLogin.JwtTokenUtil;
import com.example.backend.user.User;
import com.example.backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
public class PostController {
    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // 게시글 생성 메서드
    @PostMapping("/post/create")
    public ResponseEntity<PostResponse> createPost(HttpServletRequest request,
                                                   @RequestPart(value = "postRequest") String postRequestString,
                                                   @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) {
        try {
            Long id = getUserIdFromCookie(request);
            ObjectMapper objectMapper = new ObjectMapper();
            PostRequest postRequest = objectMapper.readValue(postRequestString, PostRequest.class);
            postRequest.setUserId(id);
            PostResponse createdPost = postService.createPost(postRequest, imageFiles);
            return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게시글 상세 조회 메서드
    @GetMapping("/post/{postId}")
    public ResponseEntity<PostResponse> getPostById(HttpServletRequest request, @PathVariable("postId") Long postId) {
        Long id = getUserIdFromCookie(request);
        PostResponse post = postService.getPostById(postId, id);
        if (post != null) {
            return new ResponseEntity<>(post, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // 게시글 목록 조회 메서드
    @GetMapping("/posts")
    public ResponseEntity<Page<PostListResponse>> getPostList(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                                              @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                              @RequestParam(value = "keyword", required = false) String keyword) {
        Page<PostListResponse> postList;
        if (categoryId != null) {
            if (keyword == null) {
                postList = postService.getPostsByCategoryId(categoryId, pageable);
            } else {
                postList = postService.searchPostsByCategoryId(categoryId, keyword, pageable);
            }
        } else {
            postList = postService.searchPosts(keyword, pageable);
        }
        return ResponseEntity.ok(postList);
    }

    // 인기 게시글 조회 메서드
    @GetMapping("/posts/best")
    public ResponseEntity<List<PostListResponse>> getPostList(@RequestParam(value = "name") String name) {
        List<PostListResponse> postList;
        if (name.equals("조회")) {
            postList = postService.getPostsByView();
        } else if (name.equals("추천")) {
            postList = postService.getPostsByLikeCount();
        } else if (name.equals("댓글")) {
            postList = postService.getPostsByCommentCount();
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(postList);
    }

    // 게시글 수정 메서드
    @PutMapping("/post/{postId}")
    public Long editPostById(@PathVariable("postId") Long postId, @RequestPart(value = "postEditRequest") String postEditRequestString,
                             @RequestPart(value = "imageFiles", required = false) List<MultipartFile> imageFiles) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        PostEditRequest postEditRequest = objectMapper.readValue(postEditRequestString, PostEditRequest.class);
        return postService.update(postId, postEditRequest, imageFiles);
    }

    // 게시글 삭제 메서드
    @DeleteMapping(value = "/post/{postId}", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> deletePostById(@PathVariable("postId") Long postId) throws Exception {
        postService.delete(postId);
        return ResponseEntity.ok("게시글 삭제 성공");
    }

    // JWT 토큰에서 사용자 ID 추출
    private Long getUserIdFromCookie(HttpServletRequest request) {
        Optional<Cookie> jwtCookie = getJwtTokenFromCookies(request.getCookies());
        if (jwtCookie.isPresent()) {
            String token = jwtCookie.get().getValue();
            return JwtTokenUtil.getId(token);
        } else {
            throw new IllegalArgumentException("JWT 토큰이 없습니다.");
        }
    }

    // 쿠키에서 JWT 토큰 쿠키를 검색
    private Optional<Cookie> getJwtTokenFromCookies(Cookie[] cookies) {
        if (cookies == null) return Optional.empty();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("jwtToken")) {
                return Optional.of(cookie);
            }
        }
        return Optional.empty();
    }
}
