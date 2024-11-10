package com.test.javatest.user.controller;

import com.test.javatest.user.dto.LoginReq;
import com.test.javatest.user.dto.LoginRes;
import com.test.javatest.user.dto.SignUpReq;
import com.test.javatest.user.dto.SignUpRes;
import com.test.javatest.user.security.JwtUtil;
import com.test.javatest.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpRes> signUp(@Valid @RequestBody SignUpReq req) {
        SignUpRes signUpRes = authService.signUp(req);
        return ResponseEntity.ok(signUpRes);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginRes> login(@RequestBody LoginReq req) {
        LoginRes loginRes = authService.login(req); // LoginRes 객체 반환
        String token = loginRes.getToken(); // 토큰 추출

        // 응답 본문에 LoginRes 객체 반환하고, 헤더에 토큰 추가
        return ResponseEntity.ok()
            .header(JwtUtil.AUTHORIZATION_HEADER, token) // Authorization 헤더에 토큰 추가
            .body(loginRes); // 응답 본문에 LoginRes 반환
    }
}
