package com.studit.domain.study.dto.study;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegionRespDto {

    private String sdNm;
    private String sggNm;
    private String mpngSn;

}
