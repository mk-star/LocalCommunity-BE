package com.example.backend.domain.post.controller;

import com.example.backend.domain.post.converter.PostConverter;
import com.example.backend.domain.post.dto.PostRequestDTO;
import com.example.backend.domain.post.dto.PostResponseDTO;
import com.example.backend.domain.post.service.PostCommandService;
import com.example.backend.domain.post.service.PostQueryService;
import com.example.backend.domain.post_image.converter.PostImageConverter;
import com.example.backend.domain.post_image.dto.PostImageResponseDTO;
import com.example.backend.domain.signLogin.JwtTokenUtil;
import com.example.backend.global.apiPayload.ApiResponse;
import com.example.backend.global.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {
    private final PostCommandService postCommandService;
    private final PostQueryService postQueryService;
    private final RedisUtil redisUtil;

    @PostMapping("")
    @Operation(summary = "게시글 생성", description = "게시글을 생성합니다.")
    public ApiResponse<PostResponseDTO.CreatePostResponseDTO> createPost(HttpServletRequest request, @Valid @RequestBody PostRequestDTO.CreatePostRequestDTO requestDTO) {
        //Long id = getUserIdFromCookie(request);
        return ApiResponse.onSuccess(PostConverter.toCreatePostResultDTO(postCommandService.createPost(requestDTO, 1L)));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "게시글 상세 조회", description = "게시글 상세 페이지를 조회합니다.")
    @Parameters({
            @Parameter(name = "postId", description = "게시글 ID")
    })
    public ApiResponse<PostResponseDTO.PostPreViewDTO> getPost(HttpServletRequest request, @PathVariable("postId") Long postId) {
        //Long id = getUserIdFromCookie(request);
        return ApiResponse.onSuccess(postQueryService.getPost(postId, 1L));
    }

    @GetMapping("")
    @Operation(summary = "게시글 목록 조회", description = "게시글 목록을 조회합니다.")
    @Parameters({
            @Parameter(name = "categoryId", description = "카테고리 ID"),
            @Parameter(name = "keyword", description = "검색 키워드"),
            @Parameter(name = "page", description = "검색 페이지, 1부터 시작합니다."),
    })
    public ApiResponse<PostResponseDTO.PostPreViewListDTO> getPostList(@RequestParam(value = "categoryId", required = false) Long categoryId,
                                                                       @RequestParam(value = "keyword", required = false) String keyword,
                                                                       @RequestParam(name = "page") Integer page) {
        return ApiResponse.onSuccess(PostConverter.postPreViewListDTO(postQueryService.getPostList(categoryId, keyword, page), redisUtil));
    }

    @Operation(summary = "게시글 수정", description = "게시글의 제목, 내용, 이미지를 수정합니다.")
    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Parameters({
            @Parameter(name = "postId", description = "게시글 ID")
    })
    public ApiResponse<PostResponseDTO.UpdatePostResponseDTO> updatePost(@PathVariable("postId") Long postId,
                                                                         @RequestPart @Valid PostRequestDTO.UpdatePostRequestDTO request,
                                                                         @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ApiResponse.onSuccess(PostConverter.toUpdatePostResultDTO(postCommandService.updatePost(postId, request, images)));
    }

    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping( "/{postId}")
    @Parameters({
            @Parameter(name = "postId", description = "게시글 ID")
    })
    public ApiResponse<String> deletePost(@PathVariable("postId") Long postId) {
        postCommandService.deletePost(postId);
        return ApiResponse.onSuccess("게시글이 성공적으로 삭제되었습니다.");
    }

    // 이미지 업로드
    @Operation(summary = "이미지 업로드", description = "게시글에 첨부할 이미지를 업로드합니다.")
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<List<PostImageResponseDTO.PostImageDTO>> uploadImages(
            @RequestPart("images") List<MultipartFile> images) {
        return ApiResponse.onSuccess(PostImageConverter.toUploadImageResultDTO(postCommandService.uploadImage(images)));
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
