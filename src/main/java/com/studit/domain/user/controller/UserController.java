package com.studit.domain.user.controller;

import com.studit.domain.user.dto.UserDTO;
import com.studit.domain.user.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserMapper userMapper;

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(createErrorResponse("인증되지 않은 사용자입니다."));
        }

        String userId = (String) authentication.getPrincipal();
        UserDTO user = userMapper.findByUserId(userId);

        if (user == null) {
            return ResponseEntity.status(404).body(createErrorResponse("사용자를 찾을 수 없습니다."));
        }

        // 비밀번호 제거
        user.setPassword(null);

        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@RequestBody UserDTO updateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(createErrorResponse("인증되지 않은 사용자입니다."));
        }

        String userId = (String) authentication.getPrincipal();

        // 본인 정보만 수정 가능
        if (!userId.equals(updateDto.getUserId())) {
            return ResponseEntity.status(403).body(createErrorResponse("권한이 없습니다."));
        }

        return ResponseEntity.ok(createSuccessResponse("사용자 정보가 수정되었습니다."));
    }

    /**
     * 토큰 검증 (프론트엔드에서 토큰 유효성 확인용)
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(createErrorResponse("유효하지 않은 토큰입니다."));
        }

        return ResponseEntity.ok(createSuccessResponse("유효한 토큰입니다."));
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