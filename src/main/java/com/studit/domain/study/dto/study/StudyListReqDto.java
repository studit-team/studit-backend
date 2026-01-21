package com.studit.domain.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyListReqDto {

    private String studyNm;
    private List<String> category;
    private List<String> mpngSn;
    private List<String> dayIds;
}
