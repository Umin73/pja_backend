package com.project.PJA.user.controller;

import com.project.PJA.common.dto.ErrorResponse;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.dto.EmailRequestDto;
import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public SuccessResponse<?> signup(@RequestBody SignupDto signupDto) {
        boolean success =userService.signup(signupDto);

        if (success) {
            return new SuccessResponse<>("success", "회원가입에 성공하였습니다", null);
        } else {
            throw new RuntimeException("회원가입에 실패하였습니다.");
        }
    }

    @PostMapping("/find-id")
    public SuccessResponse<?> findId(@RequestBody EmailRequestDto emailRequestDto) {
        log.info("== 아이디 찾기 API 진입 == email: {}", emailRequestDto.getEmail());
        Map<String, String> data = userService.findId(emailRequestDto.getEmail());
        return new SuccessResponse<>("success", "요청하신 아이디를 성공적으로 찾았습니다.", data);
    }
}
