package com.studit.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class VerificationService {

    private final RedisTemplate<String, String> redisTemplate;
    private final EmailService emailService;

    private static final String VERIFICATION_PREFIX = "email:verification:";
    private static final String VERIFIED_PREFIX = "email:verified:";
    private static final long CODE_EXPIRATION_TIME = 5;
    private static final long VERIFIED_EXPIRATION_TIME = 10;

    /**
     * 인증번호 발송
     */
    public void sendVerificationCode(String username) {  // email → username
        String verificationCode = emailService.generateVerificationCode();

        String key = VERIFICATION_PREFIX + username;
        redisTemplate.opsForValue().set(
                key,
                verificationCode,
                CODE_EXPIRATION_TIME,
                TimeUnit.MINUTES
        );

        log.info("인증번호 Redis 저장 완료 - Username: {}, Code: {}", username, verificationCode);

        emailService.sendVerificationEmail(username, verificationCode);
    }

    /**
     * 인증번호 검증
     */
    public boolean verifyCode(String username, String code) {  // email → username
        String key = VERIFICATION_PREFIX + username;
        String storedCode = redisTemplate.opsForValue().get(key);

        if (storedCode == null) {
            log.warn("인증번호 만료 또는 존재하지 않음 - Username: {}", username);
            return false;
        }

        if (!storedCode.equals(code)) {
            log.warn("인증번호 불일치 - Username: {}, Input: {}, Stored: {}", username, code, storedCode);
            return false;
        }

        log.info("인증 성공 - Username: {}", username);

        redisTemplate.delete(key);

        String verifiedKey = VERIFIED_PREFIX + username;
        redisTemplate.opsForValue().set(
                verifiedKey,
                "true",
                VERIFIED_EXPIRATION_TIME,
                TimeUnit.MINUTES
        );

        return true;
    }

    /**
     * 이메일 인증 완료 여부 확인
     */
    public boolean isEmailVerified(String username) {  // email → username
        String verifiedKey = VERIFIED_PREFIX + username;
        String verified = redisTemplate.opsForValue().get(verifiedKey);
        return "true".equals(verified);
    }

    /**
     * 인증 완료 상태 삭제
     */
    public void clearVerifiedStatus(String username) {  // email → username
        String verifiedKey = VERIFIED_PREFIX + username;
        redisTemplate.delete(verifiedKey);
        log.info("인증 완료 상태 삭제 - Username: {}", username);
    }

    /**
     * 인증번호 재발송
     */
    public void resendVerificationCode(String username) {  // email → username
        String key = VERIFICATION_PREFIX + username;
        redisTemplate.delete(key);

        sendVerificationCode(username);
        log.info("인증번호 재발송 - Username: {}", username);
    }

    /**
     * 남은 유효시간 조회
     */
    public Long getRemainingTime(String username) {  // email → username
        String key = VERIFICATION_PREFIX + username;
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ttl != null && ttl > 0 ? ttl : 0L;
    }
}