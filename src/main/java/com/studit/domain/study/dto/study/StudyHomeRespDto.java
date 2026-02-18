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
    private String categoryNames;
    private String regularDays;
    private String region;
    private String userStatus;

    private List<ScheduleRespDto> weeklySchedules;
    private List<PostListRespDto> recentNotices;
}
