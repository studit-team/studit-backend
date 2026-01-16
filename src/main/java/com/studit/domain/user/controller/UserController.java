package com.studit.domain.user.controller;

import com.studit.domain.user.dto.UserDTO;
import com.studit.domain.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/singup") // 오타 방지를 위해 기존 경로 유지
    public ResponseEntity<String> signup(@RequestBody UserDTO request) {
        userService.signup(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }
}
