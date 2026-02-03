package com.studit.domain.user.controller;

import com.studit.domain.user.dto.*;
import com.studit.domain.user.service.UserService;
import com.studit.domain.user.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final VerificationService verificationService;

    /**
     * 인증번호 발송
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody EmailVerificationRequestDto request) {
        try {
            String username = request.getUsername();

            // 이메일 형식 검증
            if (!isValidEmail(username)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("올바른 이메일 형식이 아닙니다."));
            }

            // username 중복 확인
            if (userService.existsByUsername(username)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("이미 사용 중인 이메일입니다."));
            }

            // 인증번호 발송
            verificationService.sendVerificationCode(username);

            log.info("인증번호 발송 완료 - Username: {}", username);

            return ResponseEntity.ok(createSuccessResponse("인증번호가 발송되었습니다. 이메일을 확인해주세요."));

        } catch (Exception e) {
            log.error("인증번호 발송 실패", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("인증번호 발송 중 오류가 발생했습니다."));
        }
    }

    /**
     * 인증번호 재발송
     */
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody EmailVerificationRequestDto request) {
        try {
            String username = request.getUsername();

            // 인증번호 재발송
            verificationService.resendVerificationCode(username);

            log.info("인증번호 재발송 완료 - Username: {}", username);

            return ResponseEntity.ok(createSuccessResponse("인증번호가 재발송되었습니다."));

        } catch (Exception e) {
            log.error("인증번호 재발송 실패", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("인증번호 재발송 중 오류가 발생했습니다."));
        }
    }

    /**
     * 인증번호 확인
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody EmailVerificationCheckDto request) {
        try {
            String username = request.getUsername();
            String code = request.getVerificationCode();

            // 인증번호 검증
            boolean isValid = verificationService.verifyCode(username, code);

            if (isValid) {
                log.info("인증 성공 - Username: {}", username);
                return ResponseEntity.ok(createSuccessResponse("인증이 완료되었습니다."));
            } else {
                log.warn("인증 실패 - Username: {}, Code: {}", username, code);
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("인증번호가 올바르지 않거나 만료되었습니다."));
            }

        } catch (Exception e) {
            log.error("인증 확인 중 오류 발생", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("인증 확인 중 오류가 발생했습니다."));
        }
    }

    /**
     * 남은 인증시간 조회
     */
    @GetMapping("/verification-time")
    public ResponseEntity<?> getVerificationTime(@RequestParam String username) {
        try {
            Long remainingTime = verificationService.getRemainingTime(username);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("remainingTime", remainingTime);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("시간 조회 중 오류가 발생했습니다."));
        }
    }

    /**
     * 회원가입
     */
    @PostMapping("/singup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequest) {
        try {
            String username = signupRequest.getUsername();

            // 1. 이메일 인증 완료 여부 확인
            if (!verificationService.isEmailVerified(username)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("이메일 인증을 먼저 완료해주세요."));
            }

            // 2. username 중복 확인
            if (userService.existsByUsername(username)) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("이미 사용 중인 이메일입니다."));
            }

            // 3. 비밀번호 유효성 검증
            if (!isValidPassword(signupRequest.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("비밀번호는 영문/숫자/특수문자 중 2가지 이상을 포함하여 8~32자여야 합니다."));
            }

            // 4. 비밀번호 확인
            if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("비밀번호가 일치하지 않습니다."));
            }

            // 5. 회원가입 처리
            userService.signup(signupRequest);

            // 6. 인증 완료 상태 삭제 (Redis)
            verificationService.clearVerifiedStatus(username);

            log.info("회원가입 완료 - Username: {}", username);

            return ResponseEntity.ok(createSuccessResponse("회원가입이 완료되었습니다."));

        } catch (Exception e) {
            log.error("회원가입 실패", e);
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("회원가입 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * username 중복 확인
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 형식 검증
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email != null && email.matches(emailRegex);
    }

    /**
     * 비밀번호 유효성 검증
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 32) {
            return false;
        }
        if (password.contains(" ")) {
            return false;
        }
        int count = 0;
        if (password.matches(".*[a-zA-Z].*")) count++;
        if (password.matches(".*[0-9].*")) count++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) count++;
        return count >= 2;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }
}