package com.studit.domain.study.dto.schedule;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class SubmissionDto {
    private String userId;
    private OffsetDateTime submittedAt;
    private String status;
}
