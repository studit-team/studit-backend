package com.studit.domain.study.dto.schedule;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class AssignmentRespDto {
    private int taskId;
    private int studyId;
    private String title;
    private OffsetDateTime dueDate;
    private OffsetDateTime createdAt;
    private int maxMbrNocs;
    // 과제에 속한 제출 현황 리스트
    private List<SubmissionDto> submissions;
}
