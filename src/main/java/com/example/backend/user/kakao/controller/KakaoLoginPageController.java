package com.example.backend.user.kakao.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/kakaologin")
public class KakaoLoginPageController {
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/page")
    public String loginPage(Model model) {
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + client_id
                + "&redirect_uri=" + redirect_uri
                + "&prompt=login"; // 강제 로그인 추가
        model.addAttribute("location", location);
        return "kakaoLogin";
    }

    @GetMapping("/location")
    public ResponseEntity<String> getKakaoLocation() {
        String kakaoLocation = "https://kauth.kakao.com/oauth/authorize?response_type=code"
                + "&client_id=" + client_id
                + "&redirect_uri=" + redirect_uri
                + "&prompt=login"; // 강제 로그인 추가
        return ResponseEntity.ok(kakaoLocation);
    }
}
