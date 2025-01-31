package com.example.backend.user.kakao.controller;

import com.example.backend.signLogin.JwtTokenUtil;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import com.example.backend.user.kakao.dto.KakaoUserInfoResponseDto;
import com.example.backend.user.kakao.service.KakaoJwtService;
import com.example.backend.user.kakao.service.KakaoService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * 카카오 로그인 컨트롤러
 * - 카카오 OAuth 인증 및 JWT 발급 처리
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("") // 기본 경로
public class KakaoLoginController {

    private final KakaoService kakaoService; // 카카오 서비스
    private final UserService userService; // 사용자 서비스
    private final KakaoJwtService kakaoJwtService; // JWT 서비스

    @Value("${frontend.url}")
    private String frontendUrl; // 프론트엔드 URL

    long expireTimeMs = 1000 * 60 * 60; // JWT 유효시간 60분

    /**
     * 카카오 인증 후 호출되는 콜백 엔드포인트
     * - 인가 코드를 받아 카카오 사용자 정보로 JWT 발급
     */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code, HttpServletResponse response) throws IOException {
        // 1. 카카오 인가 코드 사용해 액세스 토큰 가져옴
        String accessToken = kakaoService.getAccessTokenFromKakao(code);

        // 2. 액세스 토큰으로 사용자 정보 요청
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        // 3. 사용자 ID를 기반으로 DB에 사용자 존재 여부 확인
        String kakaoUserId = String.valueOf(userInfo.getId());
        boolean userExist = userService.checkKakaoUserExists(kakaoUserId);

        // 4. JWT 토큰 생성
        String jwtToken;

        // 5. 사용자가 DB에 존재하지 않는 경우
        if (!userExist) {
            // 신규 유저를 위한 JWT 발급
            jwtToken = kakaoJwtService.generateToken(kakaoUserId);
            // 프론트엔드 회원가입 페이지로 리다이렉트
            URI redirectUri = URI.create(frontendUrl + "/jwt-login/join?token=" + jwtToken);
            return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
        }

        // 6. DB에 존재하는 사용자일 경우
        // 사용자 정보를 기반으로 JWT 토큰 생성
        User user = userService.findByUserId(kakaoUserId);
        jwtToken = JwtTokenUtil.createToken(user.getUserId(), user.getId(), expireTimeMs);

        // 7. JWT 토큰을 쿠키에 설정
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true); // JavaScript로 접근 불가
        cookie.setMaxAge((int) (expireTimeMs / 1000)); // 만료 시간 설정
        cookie.setPath("/"); // 모든 경로에서 사용 가능
        response.addCookie(cookie);

        // 8. 캐시 비활성화 헤더 설정
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // 9. 메인페이지로 리다이렉트
        URI redirectUri = URI.create(frontendUrl + "/");
        return ResponseEntity.status(HttpStatus.FOUND).location(redirectUri).build();
    }

    /**
     * 로그아웃 처리
     * - 카카오 세션 로그아웃
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String accessToken) {
        try {
            kakaoService.logoutKakaoUser(accessToken); // 카카오 로그아웃 처리
            return ResponseEntity.ok("카카오 로그아웃 성공");
        } catch (Exception e) {
            log.error("카카오 로그아웃 실패: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카카오 로그아웃 실패");
        }
    }

    /**
     * JWT 디코딩 API
     * - 클라이언트에서 받은 JWT를 디코딩하여 사용자 정보 반환
     */
    @PostMapping("/jwt-decode")
    public ResponseEntity<?> decodeJwt(@RequestParam Map<String, String> request) {
        String token = request.get("token");
        if (token != null) {
            try {
                Claims claims = kakaoJwtService.decodeToken(token); // JWT 디코딩
                String userId = claims.getSubject(); // 사용자 ID 추출
                return ResponseEntity.ok(Map.of("userId", userId));
            } catch (Exception e) {
                log.error("JWT 디코딩 실패: ", e);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token is missing");
        }
    }
}
