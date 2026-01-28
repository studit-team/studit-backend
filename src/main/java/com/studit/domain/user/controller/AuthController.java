package com.studit.domain.user.controller;

import com.studit.domain.user.dto.SignupRequestDto;
import com.studit.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입
     */
    @PostMapping("/singup")
    public ResponseEntity<?> signup(@RequestBody SignupRequestDto signupRequest) {
        try {
            // 이메일 중복 확인
            if (userService.existsByEmail(signupRequest.getUsername())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("이미 사용 중인 이메일입니다."));
            }

            // 비밀번호 유효성 검증
            if (!isValidPassword(signupRequest.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("비밀번호는 영문/숫자/특수문자 중 2가지 이상을 포함하여 8~32자여야 합니다."));
            }

            // 비밀번호 확인
            if (!signupRequest.getPassword().equals(signupRequest.getPasswordConfirm())) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("비밀번호가 일치하지 않습니다."));
            }

            // 회원가입 처리
            userService.signup(signupRequest);

            return ResponseEntity.ok(createSuccessResponse("회원가입이 완료되었습니다."));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(createErrorResponse("회원가입 중 오류가 발생했습니다: " + e.getMessage()));
        }
    }

    /**
     * 비밀번호 암호화 테스트 (개발용)
     */
    @PostMapping("/encode-password")
    public ResponseEntity<?> encodePassword(@RequestBody Map<String, String> request) {
        String plainPassword = request.get("password");
        String encodedPassword = userService.encodePassword(plainPassword);
        
        Map<String, Object> response = new HashMap<>();
        response.put("plainPassword", plainPassword);
        response.put("encodedPassword", encodedPassword);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 이메일 중복 확인
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "이미 사용 중인 이메일입니다." : "사용 가능한 이메일입니다.");

        return ResponseEntity.ok(response);
    }

    /**
     * 비밀번호 유효성 검증
     * - 영문/숫자/특수문자 중 2가지 이상 포함
     * - 8~32자
     */
    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8 || password.length() > 32) {
            return false;
        }

        // 공백 체크
        if (password.contains(" ")) {
            return false;
        }

        int count = 0;
        if (password.matches(".*[a-zA-Z].*")) count++; // 영문
        if (password.matches(".*[0-9].*")) count++;    // 숫자
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) count++; // 특수문자

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
