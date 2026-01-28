package com.studit.domain.user.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {
    private String token;
    private String userId;
    private String name;
    private String username;
    private String authorCode;
}