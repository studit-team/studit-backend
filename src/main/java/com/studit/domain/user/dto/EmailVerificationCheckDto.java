package com.studit.domain.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerificationCheckDto {
    private String username;
    private String verificationCode;
}
