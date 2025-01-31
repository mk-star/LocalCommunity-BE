package com.example.backend.post_like;

import com.example.backend.signLogin.JwtTokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class PostLikeController {
    private final PostLikeService postLikeService;

    public PostLikeController(PostLikeService postLikeService) {
        this.postLikeService = postLikeService;
    }

    @PostMapping(value = "/post/{postId}/like", produces = "application/json; charset=utf-8")
    public ResponseEntity<String> likePost(HttpServletRequest request, @PathVariable("postId") Long postId) {
        Long id = getUserIdFromCookie(request);
        postLikeService.likePost(id, postId);
        return ResponseEntity.ok("게시글 좋아요 성공");
    }

    // JWT 토큰을 통해 userId를 쿠키에서 추출하는 메서드
    private Long getUserIdFromCookie(HttpServletRequest request) {
        Optional<Cookie> jwtCookie = getJwtTokenFromCookies(request.getCookies());
        if (jwtCookie.isPresent()) {
            String token = jwtCookie.get().getValue();
            return JwtTokenUtil.getId(token); // JWT 토큰에서 loginId 추출 (e.g., "user777")
        } else {
            throw new IllegalArgumentException("JWT 토큰이 없습니다.");
        }
    }

    // 쿠키 배열에서 JWT 토큰 쿠키를 찾아 반환하는 메서드
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
