package com.example.backend.signLogin;

import com.example.backend.user.User;
import com.example.backend.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/jwt-login")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/check-auth")
    public ResponseEntity<Map<String, Boolean>> checkAuth(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();
        boolean isAuth = false;
        boolean isKakaoUser = false;

        if(cookies != null) {
            for (Cookie cookie : cookies) {
                if("jwtToken".equals(cookie.getName())) {
                    String token = cookie.getValue();
                    isAuth = JwtTokenUtil.validateToken(token);

                    if(isAuth) {
                        String userId = JwtTokenUtil.getuserId(token);
                        User user = userService.findByUserId(userId);
                        if(user != null && user.getKakaoUser() != null) {
                            isKakaoUser = true;
                        }
                    }

                    break;
                }
            }
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("isAuth", isAuth);
        response.put("isKakaoUser", isKakaoUser);
        return ResponseEntity.ok(response);
    }
}
