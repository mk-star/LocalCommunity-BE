package com.example.backend.domain.user.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoLoginResponseDto {
    private String accessToken;
    private KakaoUserInfoResponseDto kakaoUserInfo;
    private boolean isNewUser;
}
