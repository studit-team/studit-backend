package com.studit.domain.study.controller;

import com.studit.domain.study.dto.study.StudyApplicationDto;
import com.studit.domain.study.dto.study.StudyCreateDto;
import com.studit.domain.study.dto.study.StudyListReqDto;
import com.studit.domain.study.dto.study.StudyMemberDto;
import com.studit.domain.study.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/studies")
public class StudyController {

    @Autowired
    public StudyService studyService;

    @GetMapping("/list")
    public ResponseEntity<?> getStudyList() {

        return ResponseEntity.ok(studyService.getStudyList());
    }

    @GetMapping("/search/list")
    public ResponseEntity<?> getStudySearchList(@ModelAttribute StudyListReqDto studyListReqDto) {

        return ResponseEntity.ok(studyService.searchStudyList(studyListReqDto));
    }

    @GetMapping("/{studyId}/home")
    public ResponseEntity<?> getStudySummary(@PathVariable int studyId, @RequestParam String userId) {

        return ResponseEntity.ok(studyService.getStudyHomeData(studyId, userId));
    }

    @GetMapping("/{studyId}/notice")
    public ResponseEntity<?> getStudyNotice(@PathVariable int studyId) {

        return ResponseEntity.ok(studyService.getStudyNotices(studyId));
    }
    @GetMapping("/{studyId}/schedules")
    public ResponseEntity<?> getStudySchedules(@PathVariable int studyId) {

        return ResponseEntity.ok(studyService.getStudySchedule(studyId));
    }

    @GetMapping("/{studyId}/tasks")
    public ResponseEntity<?> getStudyTasks(@PathVariable int studyId) {

        return ResponseEntity.ok(studyService.getStudyTasks(studyId));
    }
    @GetMapping("/{studyId}/board/list")
    public ResponseEntity<?> getStudyFreeBoardList(@PathVariable int studyId) {

        return ResponseEntity.ok(studyService.getStudyFreeBoardList(studyId));
    }

    @GetMapping("/region/list")
    public ResponseEntity<?> getRegionSearchList() {

        return ResponseEntity.ok(studyService.getRegionList());
    }

    @GetMapping("/category/list")
    public ResponseEntity<?> getCategoryList() {

        return ResponseEntity.ok(studyService.getCategoryList());
    }

    @PostMapping("/study")
    public ResponseEntity<String> createStudy(@RequestBody StudyCreateDto dto) {
        int result = studyService.createStudyProcess(dto);

        if (result == 1) {
            return ResponseEntity.status(HttpStatus.CREATED).body("스터디가 성공적으로 생성되었습니다. ID: " + dto.getStudyId());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("스터디 생성에 실패하였습니다.");
        }
    }

    @PostMapping("/study/apply")
    public ResponseEntity<?> applyStudy(@RequestBody StudyApplicationDto dto) {
        int result = studyService.applicationStudyProcess(dto);

        if (result == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 신청했거나 참여 중인 스터디입니다.");
        }

        return ResponseEntity.ok("신청이 완료되었습니다.");
    }

    @GetMapping("/{studyId}/applicants")
    public ResponseEntity<?> getApplicationList(@PathVariable Long studyId) {

        return ResponseEntity.ok(studyService.studyApplicationList(studyId));
    }

    @PutMapping("/{studyId}/applicants/{applicantUserId}")
    public ResponseEntity<?> updateStudyApplicationStatus(
            @PathVariable Long studyId,
            @PathVariable String applicantUserId,
            @RequestBody StudyApplicationDto applicationDto // status를 전달받기 위함
    ) {
        // 서비스 단에서 status(APPROVE/REJECT)에 따른 로직 처리 필요
        int result = studyService.updateStudyApplication(studyId, applicantUserId, applicationDto.getStatus());

        if(result == -1) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("잘못된 접근입니다.");
        }

        String message = "APPROVE".equals(applicationDto.getStatus()) ? "신청이 수락되었습니다." : "신청이 거절되었습니다.";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/{studyId}/members")
    public ResponseEntity<List<StudyMemberDto>> getStudyMembers(@PathVariable Long studyId) {
        List<StudyMemberDto> members = studyService.getStudyMemberList(studyId);
        return ResponseEntity.ok(members);
    }

    @DeleteMapping("/{studyId}/members/{userId}")
    public ResponseEntity<?> kickMember(@PathVariable Long studyId, @PathVariable String userId) {
        // 실제 서비스에서는 방장 본인은 강퇴할 수 없도록 검증 로직이 포함됩니다.
        int result = studyService.removeMember(studyId, userId);
        if(result > 0) {
            return ResponseEntity.ok("멤버가 내보내졌습니다.");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("처리 중 오류가 발생했습니다.");
    }

}