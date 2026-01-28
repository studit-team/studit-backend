package com.studit.domain.user.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {
    private String name;
    private String username;
    private String password;
    private String passwordConfirm;
    private String phone;
}