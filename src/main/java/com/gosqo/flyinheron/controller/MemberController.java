package com.gosqo.flyinheron.controller;

import com.gosqo.flyinheron.dto.member.*;
import com.gosqo.flyinheron.repository.MemberRepository;
import com.gosqo.flyinheron.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {

    private final MemberService service;
    private final MemberRepository repository;

    @PostMapping("")
    public ResponseEntity<Object> register(
            @Valid @RequestBody MemberRegisterRequest request
    ) {
        service.register(request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("회원가입에 성공했습니다.");
    }

    @PostMapping("/isUniqueEmail")
    public ResponseEntity<Object> isUniqueEmail(
            @Valid @RequestBody IsUniqueEmailRequest request
    ) {
        IsUniqueEmailResponse response = service.isUniqueEmail(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }

    @PostMapping("/isUniqueNickname")
    public ResponseEntity<Object> isUniqueNickname(
            @Valid @RequestBody IsUniqueNicknameRequest request
    ) {
        IsUniqueNicknameResponse response = service.isUniqueNickname(request);
        return ResponseEntity
                .status(response.getStatus())
                .body(response);
    }
}
