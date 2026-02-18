package com.studit.domain.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PasswordResetConfirmDto {
    private String token;       // 재설정 토큰
    private String newPassword; // 새 비밀번호
    private String newPasswordConfirm; // 새 비밀번호 확인
}
