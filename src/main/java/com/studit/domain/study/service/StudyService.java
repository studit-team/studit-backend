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

    @Transactional
    public List<StudyApplicationDto> studyApplicationList(Long studyId) {
        try {
            return studyDetailMapper.studyApplicationList(studyId);
        } catch (Exception e) {
            throw e;
        }
    }

    @Transactional
    public int updateStudyApplication(Long studyId, String applicantUserId, String status) {
        try {
            // 1. 현재 인원 정보 조회 (작성하신 getStudyHomeInfo 활용)
            // userId는 아무나 넣어도 되지만 방장 ID나 null 처리가 필요할 수 있어 임시로 applicantUserId 사용
            StudyHomeRespDto study = studyDetailMapper.getStudyHomeInfo(studyId.intValue(), applicantUserId);

            if (study == null) return -1;

            // 2. 승인(APPROVE) 처리 시 로직
            if ("APPROVE".equals(status)) {
                // 이미 작성하신 쿼리에서 카운트된 currentMbrCount와 maxMbrNocs 비교
                if (study.getCurrentMbrCount() >= study.getMaxMbrNocs()) {
                    return -1; // 인원 초과
                }

                // 스터디 멤버 테이블에 유저 추가 (mbr_status_code='APPROVED'로 등록)
                studyDetailMapper.insertStudyMemberApproved(studyId, applicantUserId);
            }

            // 3. 신청서 상태 업데이트 (WAIT -> APPROVE 또는 REJECT)
            int updatedRows = studyDetailMapper.updateApplicationStatus(studyId, applicantUserId, status);

            return updatedRows > 0 ? 1 : 0;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 멤버 목록 조회
    public List<StudyMemberDto> getStudyMemberList(Long studyId) {
        return studyDetailMapper.getStudyMemberList(studyId);
    }

    // 멤버 강퇴 (삭제)
    @Transactional
    public int removeMember(Long studyId, String userId) {
        try {
            // 1. 멤버 삭제 (study_mbr 테이블)
            int result = studyDetailMapper.deleteStudyMember(studyId, userId);

            // 2. 신청서 테이블에서도 삭제하거나 상태를 변경 (선택 사항)
            // studyDetailMapper.updateApplicationStatus(studyId, userId, "KICKED");

            return result;
        } catch (Exception e) {
            throw e;
        }
    }


}
