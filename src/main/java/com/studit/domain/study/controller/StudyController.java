package com.studit.domain.study.controller;

import com.studit.domain.study.dto.study.StudyApplicationDto;
import com.studit.domain.study.dto.study.StudyCreateDto;
import com.studit.domain.study.dto.study.StudyListReqDto;
import com.studit.domain.study.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



}
