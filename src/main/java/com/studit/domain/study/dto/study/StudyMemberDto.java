package com.studit.domain.study.dto.study;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyMemberDto {
    private String userId;
    private String name;
    private String username;
    private String roleCode;
    private String mbrStatusCode;
    private LocalDateTime joinDate;
}