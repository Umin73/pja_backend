package com.project.PJA.user.controller;

import com.project.PJA.common.dto.ErrorResponse;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.repository.UserRepository;
import com.project.PJA.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public SuccessResponse<?> signup(@RequestBody SignupDto signupDto) throws Exception, ErrorResponse {
        boolean success =userService.signup(signupDto);

        if (success) {
            return new SuccessResponse<>("success", "회원가입에 성공하였습니다", null);
        } else {
            throw new ErrorResponse("error", "서버 오류로 인해 회원가입에 실패했습니다.");
        }
    }
}
