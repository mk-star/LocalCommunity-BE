package com.example.backend.mail;

import lombok.Data;

@Data
public class ApproveRequestDto {    // 인증번호 받은 후 인증 요청
    private String email;
    private String code;
}
