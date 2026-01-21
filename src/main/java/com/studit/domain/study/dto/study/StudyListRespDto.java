package com.studit.domain.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyListRespDto {

    private int studyId;
    private String leaderId;
    private String studyNm;
    private String studyDc;
    private int maxMbrNocs;
    private String studyStatusCode;
    private String categoryNames;
    private String sggNm;
    private Long currentMbrCnt;
    private String dayNames;
}
