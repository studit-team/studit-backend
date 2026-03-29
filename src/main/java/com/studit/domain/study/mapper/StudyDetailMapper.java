package com.studit.domain.study.mapper;

import com.studit.domain.study.dto.post.PostDetailRespDto;
import com.studit.domain.study.dto.post.PostListRespDto;
import com.studit.domain.study.dto.schedule.AssignmentRespDto;
import com.studit.domain.study.dto.schedule.ScheduleRespDto;
import com.studit.domain.study.dto.study.StudyApplicationDto;
import com.studit.domain.study.dto.study.StudyCreateDto;
import com.studit.domain.study.dto.study.StudyHomeRespDto;
import com.studit.domain.study.dto.study.StudyMemberDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyDetailMapper {

    /**
     * 스터디 홈 상단 배너 및 기본 정보 조회
     * (study, study_mbr, study_day_mapping 테이블 활용)
     */
    StudyHomeRespDto getStudyHomeInfo(@Param("studyId") int studyId, @Param("userId") String userId);

    /**
     * 이번 주 스터디 일정 리스트 조회
     * (study_schedule 테이블 활용)
     */
    List<ScheduleRespDto> getWeeklySchedules(@Param("studyId") int studyId);

    /**
     * 스터디 일정 리스트 조회
     * (study_schedule 테이블 활용)
     */
    List<ScheduleRespDto> getStudySchedules(@Param("studyId") int studyId);

    /**
     * 최신 공지사항 2건 조회
     * (board 테이블 활용)
     */
    List<PostListRespDto> getRecentNotices(@Param("studyId") int studyId);

    /**
     * 스터디 공지사항 조회
     * (board 테이블 활용)
     */
    List<PostDetailRespDto> getStudyNotices(@Param("studyId") int studyId);

    /**
     * 스터디 과제 조회
     * (task 테이블 활용)
     */
    List<AssignmentRespDto> getStudyTasks(@Param("studyId") int studyId);

    /**
     * 스터디 자유게시판 조회
     * (board 테이블 활용)
     */
    List<PostListRespDto> getStudyFreeBoardList(@Param("studyId") int studyId);

    /**
     * 1. 스터디 기본 정보 생성
     * 2. 스터디 카테고리 매핑 정보 저장
     * 3. 스터디 진행 요일 정보 저장
     * 4. 스터디 멤버 등록 (최초 생성 시 방장 자동 등록)
     * 5. 스터디 초기 비용 설정 (보증금/참가비 등)
     */
    int insertStudy(StudyCreateDto dto);
    int insertStudyCategory(StudyCreateDto dto);
    int insertStudyDayOfWeeks(StudyCreateDto dto);
    int insertStudyMember(StudyCreateDto dto);
    int insertStudyFee(StudyCreateDto dto);

    /**
     * 1. 스터디 신청
     * 2. 스터디 신청 중복확인
     * (applications 테이블 활용)
     * */
    int applicationStudy(StudyApplicationDto dto);
    int checkAlreadyApplied(StudyApplicationDto dto);

    /**     * 1. 스터디 신청 목록 조회 (반드시 List로 변경해야 여러명이 보입니다)
     */
    List<StudyApplicationDto> studyApplicationList(@Param("studyId") Long studyId);

    /**
     * 2. 신청서 상태 업데이트 (WAIT -> APPROVE / REJECT)
     */
    int updateApplicationStatus(@Param("studyId") Long studyId,
                                @Param("userId") String userId,
                                @Param("status") String status);

    /**
     * 3. 승인 시 스터디 멤버로 추가 (insertStudyMember와 비슷하지만 파라미터 구성이 다름)
     */
    int insertStudyMemberApproved(@Param("studyId") Long studyId, @Param("userId") String userId);

    /**
     * 스터디 멤버 목록 조회
     */
    List<StudyMemberDto> getStudyMemberList(@Param("studyId") Long studyId);

    /**
     * 스터디 멤버 삭제 (강퇴)
     */
    int deleteStudyMember(@Param("studyId") Long studyId, @Param("userId") String userId);


}