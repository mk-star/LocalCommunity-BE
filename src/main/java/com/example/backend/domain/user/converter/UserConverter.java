package com.example.backend.domain.user.converter;

import com.example.backend.domain.user.User;
import com.example.backend.domain.user.dto.UserResponseDTO;

public class UserConverter {
    public static UserResponseDTO.UserInfoResponseDTO toUserInfo(User user) {
        return UserResponseDTO.UserInfoResponseDTO.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .profileImage(user.getProfile_url())
                .build();
    }
}
