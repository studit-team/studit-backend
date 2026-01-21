package com.studit.domain.study.dto.schedule;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class ScheduleRespDto {
    private Long scheduleId;
    private String title;
    private String description;
    private OffsetDateTime meetingAt;
    private String location;
    private String status;
}
