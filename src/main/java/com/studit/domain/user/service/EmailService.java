package com.studit.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;


@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * 6자리 인증번호 생성
     */
    public String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    /**
     * 인증 이메일 발송
     */
    public void sendVerificationEmail(String username, String verificationCode) {  // toEmail → username
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(username);  // username이 이메일 형식
            message.setSubject("[Studit] 회원가입 인증번호");
            message.setText(
                    "안녕하세요, Studit입니다.\n\n" +
                            "회원가입을 위한 인증번호는 다음과 같습니다:\n\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━\n" +
                            "    인증번호: " + verificationCode + "\n" +
                            "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                            "이 인증번호는 5분간 유효합니다.\n" +
                            "본인이 요청하지 않았다면 이 메일을 무시해주세요.\n\n" +
                            "감사합니다.\n" +
                            "Studit 드림"
            );

            mailSender.send(message);
            log.info("인증 이메일 발송 완료: {}", username);

        } catch (Exception e) {
            log.error("이메일 발송 실패: {}", username, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }

    /**
     * SimpleMailMessage 직접 전송 (PasswordResetService에서 사용)
     */
    public void sendRawEmail(SimpleMailMessage message) {
        message.setFrom(fromEmail);
        mailSender.send(message);
    }
}