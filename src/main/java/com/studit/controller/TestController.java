package com.studit.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/studies/test")
    public List<Map<String, Object>> getTestStudies() {
        List<Map<String, Object>> studies = new ArrayList<>();

        // 첫 번째 더미 데이터
        Map<String, Object> study1 = new HashMap<>();
        study1.put("study_id", "STD001");
        study1.put("study_nm", "스프링부트 챌린저");
        study1.put("category", "백엔드");
        study1.put("max_mbr_nocs", 5);

        // 두 번째 더미 데이터
        Map<String, Object> study2 = new HashMap<>();
        study2.put("study_id", "STD002");
        study2.put("study_nm", "Vite + React 마스터");
        study2.put("category", "프론트엔드");
        study2.put("max_mbr_nocs", 8);

        studies.add(study1);
        studies.add(study2);

        return studies;
    }
}