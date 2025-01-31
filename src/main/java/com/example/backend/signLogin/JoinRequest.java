package com.example.backend.signLogin;

import com.example.backend.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "로그인 아이디가 비어있습니다.")
    private String userId;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;
    private String username;
    private String address;
    private String phone;
    private String email;
    private String nickname;
    private String kakaoUser;



    public User toEntity(String encodedPassword, String defaultProfileImageUrl) {
        return User.builder()
                .userId(this.userId)
                .password(encodedPassword)
                .username(this.username)
                .address(this.address)
                .phone(this.phone)
                .email(this.email)
                .nickname(this.nickname)
                .kakaoUser(this.kakaoUser)
                .profile_url(defaultProfileImageUrl) // 설정 파일에서 가져온 기본 프로필 URL 사용
                .build();
    }
}