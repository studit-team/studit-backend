package com.studit.domain.study.service;

import com.studit.domain.study.dto.post.PostDetailRespDto;
import com.studit.domain.study.dto.post.PostListRespDto;
import com.studit.domain.study.dto.schedule.ScheduleRespDto;
import com.studit.domain.study.dto.study.*;
import com.studit.domain.study.entity.Study;
import com.studit.domain.study.mapper.StudyDetailMapper;
import com.studit.domain.study.mapper.StudyListMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudyService {

    @Autowired
    public StudyListMapper studyListMapper;
    @Autowired
    public StudyDetailMapper studyDetailMapper;

    @Transactional(rollbackFor = Exception.class)
    public List<StudyListRespDto> getStudyList() {
        List<Study> studyList = studyListMapper.getStudyList();
        List<StudyListRespDto> studyListRespDtos = new ArrayList<>();

        for (Study study : studyList) {
            StudyListRespDto dto = StudyListRespDto.builder()
                    .studyId(study.getStudyId())
                    .leaderId(study.getLeaderId())
                    .studyNm(study.getStudyNm())
                    .studyDc(study.getStudyDc())
                    .maxMbrNocs(study.getMaxMbrNocs())
                    .studyStatusCode(study.getStudyStatusCode())
                    .build();

            studyListRespDtos.add(dto);
        }

        return studyListRespDtos;
    }

    @Transactional(readOnly = true)
    public List<StudyListRespDto> searchStudyList(StudyListReqDto studyListReqDto) {

        return studyListMapper.searchStudyList(studyListReqDto);
    }

    @Transactional(readOnly = true)
    public StudyHomeRespDto getStudyHomeData(int studyId) {
        // 1. 기본 정보 및 인원/요일 정보 조회
        StudyHomeRespDto homeData = studyDetailMapper.getStudyHomeInfo(studyId);
        System.out.printf("" + homeData.getCurrentMbrCount());
        if (homeData != null) {
            // 2. 이번 주 일정 조회
            List<ScheduleRespDto> schedules = studyDetailMapper.getWeeklySchedules(studyId);
            homeData.setWeeklySchedules(schedules);

            // 3. 최신 공지사항 조회
            List<PostListRespDto> notices = studyDetailMapper.getRecentNotices(studyId);
            homeData.setRecentNotices(notices);
        }

        return homeData;
    }

    @Transactional(readOnly = true)
    public List<RegionRespDto> getRegionList() {
        return studyListMapper.getRegionList();
    }

    public List<CategoryRespDto> getCategoryList() {
            return studyListMapper.getCategoryList();
    }

    public List<PostDetailRespDto> getStudyNotices(int studyId) {
        return studyDetailMapper.getStudyNotices(studyId);
    }

    public List<ScheduleRespDto> getStudySchedule(int studyId) {
        return studyDetailMapper.getStudySchedules(studyId);
    }



}
