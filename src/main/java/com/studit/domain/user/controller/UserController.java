package com.studit.domain.user.controller;

import jakarta.servlet.http.HttpSession;
import lombok.val;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {

    @PostMapping(value ="user/singup")
    public void signup(HttpSession httpSession, Model model) {
        System.out.println("회원가입시도");
    }
}
