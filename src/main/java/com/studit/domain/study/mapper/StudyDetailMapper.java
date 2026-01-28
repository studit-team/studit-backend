package com.studit.domain.study.mapper;

import com.studit.domain.study.dto.post.PostDetailRespDto;
import com.studit.domain.study.dto.post.PostListRespDto;
import com.studit.domain.study.dto.schedule.AssignmentRespDto;
import com.studit.domain.study.dto.schedule.ScheduleRespDto;
import com.studit.domain.study.dto.study.StudyHomeRespDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudyDetailMapper {

    /**
     * 스터디 홈 상단 배너 및 기본 정보 조회
     * (study, study_mbr, study_day_mapping 테이블 활용)
     */
    StudyHomeRespDto getStudyHomeInfo(@Param("studyId") int studyId);

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
}