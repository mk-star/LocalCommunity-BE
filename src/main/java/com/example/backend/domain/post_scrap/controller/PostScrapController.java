package com.example.backend.domain.post_scrap;

import com.example.backend.domain.post_scrap.service.PostScrapCommandService;
import com.example.backend.domain.signLogin.JwtTokenUtil;
import com.example.backend.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}")
public class PostScrapController {
    private final PostScrapCommandService postScrapCommandService;

    @Operation(summary = "스크랩/스크랩 취소", description = "스크랩을 했다면 취소하고 안 했다면 합니다.")
    @PostMapping("/scrap")
    @Parameters({
            @Parameter(name = "postId", description = "게시글 ID")
    })
    public ApiResponse<String> scrapPost(HttpServletRequest request, @PathVariable("postId") Long postId) {
        //Long id = getUserIdFromCookie(request);
        postScrapCommandService.scrapPost(postId, 1L);
        return ApiResponse.onSuccess("스크랩/스크랩 취소가 성공적으로 완료되었습니다.");
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
