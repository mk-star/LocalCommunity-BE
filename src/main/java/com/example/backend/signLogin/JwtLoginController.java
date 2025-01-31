package com.example.backend.signLogin;
import com.example.backend.user.User;
import com.example.backend.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/jwt-login")
public class JwtLoginController {

    private final UserService userService;

    @GetMapping("/")
    public String home(Authentication auth) {
        if(auth != null) {
            User loginUser = userService.getLoginUserByuserId(auth.getName());
        }
        return "Jwt Token 화면 로그인";
    }

    @GetMapping("/join")
    public String joinPage() {
        return "Jwt Token 화면 로그인";
    }

    @PostMapping("/join")
    public String join(@Valid @RequestBody JoinRequest joinRequest, BindingResult bindingResult) {
        // userId 중복 체크
        if(!isKakaoLogin(joinRequest) && userService.checkuserIdDuplicate(joinRequest.getUserId())) {
            bindingResult.addError(new FieldError("joinRequest", "userId", "로그인 아이디가 중복됩니다.")); // 여기선 빼도 될것같은데??
        }

        // password와 passwordCheck가 같은지 체크
        if(!isKakaoLogin(joinRequest) && !joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            bindingResult.addError(new FieldError("joinRequest", "passwordCheck", "비밀번호가 일치하지 않습니다."));
        }

        if (bindingResult.hasErrors()) {
            System.out.println("회원가입 실패. 에러 목록:");
            bindingResult.getAllErrors().forEach(error -> {
                System.out.println(error.toString());
            });
            return "회원가입 실패";
        }

        userService.join2(joinRequest);
        return "회원가입 성공";
    }

    private boolean isKakaoLogin(JoinRequest joinRequest) {
        return joinRequest.getKakaoUser() != null && !joinRequest.getKakaoUser().isEmpty();
    }

    @GetMapping("/check-id")
    public Map<String, Object> checkId(@RequestParam(name = "userId") String userId) {
        Map<String, Object> response = new HashMap<>();

        if(userService.checkuserIdDuplicate(userId)) {
            response.put("success", false);
            response.put("message", "이미 존재하는 아이디입니다.");
        } else {
            response.put("success", true);
            response.put("message", "사용 가능한 아이디입니다.");
        }

        return response;
    }

    @GetMapping("/check-nickname")
    public Map<String, Object> checkNickname(@RequestParam(name = "nickname") String nickname) {
        Map<String, Object> response = new HashMap<>();

        if(userService.checkNicknameDuplicate(nickname)) {
            response.put("success", false);
            response.put("message", "이미 사용중인 닉네임입니다.");
        } else {
            response.put("success", true);
            response.put("message", "사용 가능한 닉네임입니다.");
        }

        return response;
    }

    @GetMapping("/check-kakaouser")
    public Map<String, Object> checkKakaoUser(@RequestParam(name = "kakaoUser") String kakaoUser) {
        Map<String, Object> response = new HashMap<>();

        if(userService.checkKakaoUserExists(kakaoUser)) {
            response.put("success", true);
            response.put("message", "존재하지 않는 회원입니다.");
        } else {
            response.put("success", false);
            response.put("message", "로그인 성공");
        }

        return response;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "Jwt Token 화면 로그인";
    }


    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        User user = userService.login(loginRequest);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패");
        }

        long expireTimeMs = 1000 * 60 * 60; // 60분 유효
        String jwtToken = JwtTokenUtil.createToken(user.getUserId(), user.getId(), expireTimeMs);  // userId 기반으로 토큰 생성

        // 로그 추가 - 로그인된 사용자 userId 확인
        System.out.println("Logged in userId: " + user.getUserId());

        // 쿠키에 JWT 토큰 저장
        Cookie cookie = new Cookie("jwtToken", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) (expireTimeMs / 1000)); // 쿠키 만료 시간 설정
        cookie.setPath("/");
        response.addCookie(cookie);

        // 캐시 비활성화 헤더 설정
        response.addCookie(cookie);
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        return ResponseEntity.ok("로그인 성공");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        // 쿠키 파기
        Cookie cookie = new Cookie("jwtToken", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/info")
    public User userInfo(Authentication auth) {
        return userService.getLoginUserByuserId(auth.getName());
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "Jwt Token 화면 로그인";
    }

    @GetMapping("/authentication-fail")
    public String authenticationFail() {
        return "인증 실패";
    }

    @GetMapping("/authorization-fail")
    public String authorizationFail() {
        return "권한 실패";
    }

    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdraw(@RequestParam(name = "userId") String userId) {
        User user = userService.findByUserId(userId);
        Long id = user.getId();

        boolean isDeleted;

        if(user.getKakaoUser().isEmpty()) {
            isDeleted = userService.deleteUser(id);
        } else {
            isDeleted = userService.deleteKakaoUser(id,userId);
        }

        if(isDeleted) {
            return ResponseEntity.ok("회원 탈퇴 성공");
        } else {
            return ResponseEntity.status(500).body("회원 탈퇴 실패");
        }
    }
}