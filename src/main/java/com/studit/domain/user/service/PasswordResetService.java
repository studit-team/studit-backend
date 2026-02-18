package com.studit.domain.user.service;

import com.studit.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // Redis 키 prefix
    private static final String RESET_TOKEN_PREFIX = "password:reset:";
    // 토큰 유효시간: 30분
    private static final long TOKEN_EXPIRATION_MINUTES = 30;
    // 비밀번호 재설정 링크 (프론트엔드 주소)
    private static final String RESET_URL = "http://localhost:5173/reset-password";

    /**
     * 비밀번호 재설정 링크 발송
     * 1. 가입된 이메일인지 확인
     * 2. UUID 토큰 생성 후 Redis에 저장 (username 매핑)
     * 3. 재설정 링크 이메일 발송
     */
    public void sendPasswordResetLink(String username) {
        // 1. 가입 여부 확인
        if (userMapper.findByUsername(username) == null) {
            // 보안상 가입되지 않은 이메일이어도 동일한 응답 (사용자 열거 방지)
            log.warn("비밀번호 재설정 요청 - 가입되지 않은 이메일: {}", username);
            return;
        }

        // 2. 기존 토큰이 있으면 삭제 후 새 토큰 발급
        String existingToken = redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + "email:" + username);
        if (existingToken != null) {
            redisTemplate.delete(RESET_TOKEN_PREFIX + existingToken);
        }
        redisTemplate.delete(RESET_TOKEN_PREFIX + "email:" + username);

        // 3. UUID 토큰 생성
        String token = UUID.randomUUID().toString();

        // 4. Redis에 저장: token → username, email → token (양방향)
        redisTemplate.opsForValue().set(
                RESET_TOKEN_PREFIX + token,
                username,
                TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );
        redisTemplate.opsForValue().set(
                RESET_TOKEN_PREFIX + "email:" + username,
                token,
                TOKEN_EXPIRATION_MINUTES,
                TimeUnit.MINUTES
        );

        // 5. 이메일 발송
        sendPasswordResetEmail(username, token);

        log.info("비밀번호 재설정 링크 발송 완료 - Username: {}", username);
    }

    /**
     * 토큰 유효성 검증
     */
    public boolean validateResetToken(String token) {
        String username = redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + token);
        return username != null;
    }

    /**
     * 비밀번호 재설정
     * 1. 토큰으로 username 조회
     * 2. 비밀번호 암호화 후 DB 업데이트
     * 3. Redis 토큰 삭제
     */
    public void resetPassword(String token, String newPassword) {
        // 1. 토큰으로 username 조회
        String username = redisTemplate.opsForValue().get(RESET_TOKEN_PREFIX + token);
        if (username == null) {
            throw new IllegalArgumentException("유효하지 않거나 만료된 토큰입니다.");
        }

        // 2. 비밀번호 암호화 후 DB 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(username, encodedPassword);

        // 3. Redis 토큰 삭제 (재사용 방지)
        redisTemplate.delete(RESET_TOKEN_PREFIX + token);
        redisTemplate.delete(RESET_TOKEN_PREFIX + "email:" + username);

        log.info("비밀번호 재설정 완료 - Username: {}", username);
    }

    /**
     * 비밀번호 재설정 이메일 발송
     */
    private void sendPasswordResetEmail(String username, String token) {
        String resetLink = RESET_URL + "?token=" + token;

        try {
            org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
            message.setTo(username);
            message.setSubject("[Studit] 비밀번호 재설정 안내");
            message.setText(
                    "안녕하세요, Studit입니다.\n\n" +
                    "비밀번호 재설정을 요청하셨습니다.\n" +
                    "아래 링크를 클릭하여 비밀번호를 변경해주세요.\n\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━\n" +
                    "  " + resetLink + "\n" +
                    "━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                    "⚠ 이 링크는 30분간 유효합니다.\n" +
                    "본인이 요청하지 않았다면 이 메일을 무시해주세요.\n\n" +
                    "감사합니다.\n" +
                    "Studit 드림"
            );

            // EmailService의 mailSender를 직접 사용하지 않으므로 별도 주입
            emailService.sendRawEmail(message);

            log.info("비밀번호 재설정 이메일 발송 - Username: {}", username);
        } catch (Exception e) {
            log.error("비밀번호 재설정 이메일 발송 실패 - Username: {}", username, e);
            throw new RuntimeException("이메일 발송에 실패했습니다.", e);
        }
    }
}
