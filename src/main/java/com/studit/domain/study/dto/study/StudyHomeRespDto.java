package com.studit.domain.study.dto.study;

import com.studit.domain.study.dto.post.PostListRespDto;
import com.studit.domain.study.dto.schedule.ScheduleRespDto;
import lombok.Data;

import java.util.List;

@Data
public class StudyHomeRespDto {
    private Long studyId;
    private String studyNm;
    private String studyDc;
    private int maxMbrNocs;
    private int currentMbrCount;
    private List<String> categoryIds;
    private String regularDays;

    private List<ScheduleRespDto> weeklySchedules;
    private List<PostListRespDto> recentNotices;
}
