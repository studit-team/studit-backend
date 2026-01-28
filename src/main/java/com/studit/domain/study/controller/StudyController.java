package com.studit.domain.study.controller;

import com.studit.domain.study.dto.study.StudyListReqDto;
import com.studit.domain.study.service.StudyService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<?> getStudySummary(@PathVariable int studyId) {

        return ResponseEntity.ok(studyService.getStudyHomeData(studyId));
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


}
