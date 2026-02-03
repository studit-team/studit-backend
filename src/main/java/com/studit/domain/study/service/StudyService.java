package com.studit.domain.study.service;

import com.studit.domain.study.dto.post.PostDetailRespDto;
import com.studit.domain.study.dto.post.PostListRespDto;
import com.studit.domain.study.dto.schedule.AssignmentRespDto;
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
    public StudyHomeRespDto getStudyHomeData(int studyId, String userId) {
        StudyHomeRespDto homeData = studyDetailMapper.getStudyHomeInfo(studyId, userId);
        if (homeData != null) {
            List<ScheduleRespDto> schedules = studyDetailMapper.getWeeklySchedules(studyId);
            homeData.setWeeklySchedules(schedules);

            List<PostListRespDto> notices = studyDetailMapper.getRecentNotices(studyId);
            homeData.setRecentNotices(notices);
        }

        return homeData;
    }

    @Transactional(readOnly = true)
    public List<RegionRespDto> getRegionList() {
        return studyListMapper.getRegionList();
    }
    @Transactional(readOnly = true)
    public List<CategoryRespDto> getCategoryList() {
            return studyListMapper.getCategoryList();
    }
    @Transactional(readOnly = true)
    public List<PostDetailRespDto> getStudyNotices(int studyId) {
        return studyDetailMapper.getStudyNotices(studyId);
    }
    @Transactional(readOnly = true)
    public List<ScheduleRespDto> getStudySchedule(int studyId) {
        return studyDetailMapper.getStudySchedules(studyId);
    }
    @Transactional(readOnly = true)
    public List<AssignmentRespDto> getStudyTasks(int studyId) {
        return studyDetailMapper.getStudyTasks(studyId);
    }
    @Transactional(readOnly = true)
    public List<PostListRespDto> getStudyFreeBoardList(int studyId) {
        return studyDetailMapper.getStudyFreeBoardList(studyId);
    }
    @Transactional
    public int createStudyProcess(StudyCreateDto dto) {
        try {
            if (studyDetailMapper.insertStudy(dto) == 0) return 0;
            if (dto.getCategoryIds() != null && !dto.getCategoryIds().isEmpty()) {
                studyDetailMapper.insertStudyCategory(dto);
            }
            if (dto.getDayIds() != null && !dto.getDayIds().isEmpty()) {
                studyDetailMapper.insertStudyDayOfWeeks(dto);
            }
            if (studyDetailMapper.insertStudyMember(dto) == 0) {
                throw new RuntimeException("방장 등록에 실패하였습니다.");
            }
            if (dto.getFees() != null && !dto.getFees().isEmpty()) {
                studyDetailMapper.insertStudyFee(dto);
            }
            return 1;

        } catch (Exception e) {
            throw e;
        }
    }
    @Transactional
    public int applicationStudyProcess(StudyApplicationDto dto) {
        try {
            if (studyDetailMapper.checkAlreadyApplied(dto) > 0) {
                return -1;
            }
            return studyDetailMapper.applicationStudy(dto);
        } catch (Exception e) {
            throw e;
        }
    }




}
