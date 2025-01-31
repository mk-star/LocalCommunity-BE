package com.example.backend.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/jwt-login/id-email")
    public void IdMailSend(@RequestBody MailDto mailDto) {
        mailService.sendFindIdMail(mailDto);
    }

    @PostMapping("/jwt-login/pw-email")
    public void PwMailSend(@RequestBody MailDto mailDto) {
        String token = mailService.generatePwResetToken(mailDto.getEmail());
        mailService.sendPwResetMail(mailDto, token);
    }

    @PostMapping("/jwt-login/verify-code")
    public Map<String, Object> verifyCode(@RequestBody ApproveRequestDto approveRequestDto) {
        Map<String, Object> response = new HashMap<>();

        if(mailService.verifyCode(approveRequestDto)) {
            response.put("success", true);
            response.put("message", "인증되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "인증에 실패하였습니다.");
        }

        return response;
    }


}
