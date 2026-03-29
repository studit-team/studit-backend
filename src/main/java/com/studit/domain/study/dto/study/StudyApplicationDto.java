package com.studit.domain.study.dto.study;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class StudyApplicationDto {

    private int studyId;
    private String userId;
    private String content;
    private String status;
    private OffsetDateTime appliedAt;
    private String username;
}
