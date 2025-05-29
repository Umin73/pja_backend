package com.project.PJA.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.PJA.common.dto.SuccessResponse;
import com.project.PJA.security.config.SecurityConfig;
import com.project.PJA.security.jwt.JwtAuthenticationFilter;
import com.project.PJA.security.jwt.JwtTokenProvider;
import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class, excludeAutoConfiguration = SecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccess() throws Exception {
        SignupDto signupDto = new SignupDto("testuserId", "testpw1234", "testuser", "testuser@gmail.com");

        Mockito.when(userService.signup(any(SignupDto.class))).thenReturn(true);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공하였습니다"));
    }

    @Test
    @DisplayName("회원가입 실패 테스트")
    void signupFail() throws Exception {
        SignupDto signupDto = new SignupDto("testuserId", "testpw1234", "testuser", "testuser@gmail.com");

        Mockito.when(userService.signup(any(SignupDto.class))).thenReturn(false);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("fail"))
                .andExpect(jsonPath("$.message").exists());
    }
}
