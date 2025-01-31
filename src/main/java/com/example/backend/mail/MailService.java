package com.example.backend.mail;

import com.example.backend.config.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisService redisService;

    private static String senderEmail = "comtownddwu@naver.com";
    private static int authCode;
    private static String pwLink = "http://localhost:3000/jwt-login/change-pw";

    public static void createAuthCode() {
        authCode = (int)(Math.random()*90000) + 100000;
    }

    public String generatePwResetToken(String email) {
        String token = UUID.randomUUID().toString();
        /*redisService.save(email, token);*/
        redisService.save(token, email);
        return token;
    }

    public MimeMessage createFindIdMail(String mail) {

        createAuthCode();

        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("[TOWN-IN] 이메일 인증");

            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + authCode + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";

            message.setText(body,"UTF-8", "html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public MimeMessage createFindPwMail(String mail, String token) {
        MimeMessage message = javaMailSender.createMimeMessage();

        try{
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            message.setSubject("[TOWN-IN] 비밀번호 재설정 링크");

            String body = "";
            body += "<h3>" + "비밀번호 재설정 링크입니다." + "</h3>";
            body += "<p>아래 링크를 클릭해 비밀번호를 재설정 해주세요.</p>";
            body += "<a href='" + pwLink + "?token=" + token + "'>비밀번호 재설정</a>";

            message.setText(body,"UTF-8", "html");

        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return message;
    }

    public int sendFindIdMail(MailDto mailDto) {
        String mail = mailDto.getEmail().trim();
        MimeMessage message = createFindIdMail(mail);

        javaMailSender.send(message);
        redisService.save(mail, String.valueOf(authCode));
        return authCode;
    }

    public int sendPwResetMail(MailDto mailDto, String token) {
        String mail = mailDto.getEmail().trim();
        MimeMessage message = createFindPwMail(mail, token);

        javaMailSender.send(message);
        redisService.save(mail, String.valueOf(authCode));
        return authCode;
    }

    public boolean verifyCode(ApproveRequestDto approveRequestDto) {
        String storedCode = redisService.get(approveRequestDto.getEmail());

        if(storedCode != null && storedCode.equals(approveRequestDto.getCode())) {
            redisService.delete(approveRequestDto.getEmail());
            return true;
        } else {
            return false;
        }
    }
}
