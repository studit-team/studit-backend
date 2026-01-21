package com.studit.domain.study.mapper;

import com.studit.domain.study.dto.study.CategoryRespDto;
import com.studit.domain.study.dto.study.RegionRespDto;
import com.studit.domain.study.dto.study.StudyListReqDto;
import com.studit.domain.study.dto.study.StudyListRespDto;
import com.studit.domain.study.entity.Study;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StudyListMapper {

    List<Study> getStudyList();

    List<StudyListRespDto> searchStudyList(StudyListReqDto studyListReqDto);

    List<RegionRespDto> getRegionList();

    List<CategoryRespDto> getCategoryList();

}
