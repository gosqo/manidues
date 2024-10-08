package com.gosqo.flyinheron.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MemberViewController {

    @GetMapping("/login")
    public String viewLogin() {
        return "member/login";
    }

    @GetMapping("/signUp")
    public String viewSignUp() {
        return "member/signUp";
    }
}
