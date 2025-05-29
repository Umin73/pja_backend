package com.project.PJA.user.service;

import ch.qos.logback.core.subst.Token;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.security.service.EmailVerificationService;
import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.entity.UserRole;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;

    public boolean signup(SignupDto signupDto) {

        Users user = new Users();
        user.setUid(signupDto.getUid());
        user.setName(signupDto.getName());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setEmail(signupDto.getEmail());
        user.setRole(UserRole.ROLE_USER);
        user.setEmailVerified(false);

        userRepository.save(user);
        return true;
    }

    // 인증 이메일 보내기
    public void sendVerificationEmail(String email) {
        log.info("email: {}" , email);
        Optional<Users> optionalUser = userRepository.findByEmail(email);
        if(optionalUser.isPresent()) {
            Users user = optionalUser.get();
            log.info("user:{}",user);

            if (user.isEmailVerified()) {
                throw new RuntimeException("이미 이메일 인증이 완료되었습니다.");
            }
            String token = UUID.randomUUID().toString(); // 토큰 생성
            log.info("token: {}", token);
            emailVerificationService.saveEmailVerificationToken(user.getEmail(), token, 60*24); // 토큰 24시간동안 유효함

            // 이메일 보내기 추가 필요
        } else {
            throw new NotFoundException("일치하는 사용자를 찾을 수 없습니다.");
        }
    }

    // 이메일 인증하기
    public void verifyEmail(String email, String token) {
        Optional<Users> optionalUser = userRepository.findByEmail(email);

        if(optionalUser.isPresent()) {
            Users user = optionalUser.get();

            String storedToken = emailVerificationService.getEmailVerificationToken(user.getEmail());
            if(storedToken == null || !storedToken.equals(token)) {
                throw new UnauthorizedException("유효하지 않거나 만료된 인증 토큰 입니다.");
            }

            user.setEmailVerified(true);
            userRepository.save(user);

            // 인증된 이메일 토큰은 삭제
            emailVerificationService.deleteEmailVerificationToken(email);
        } else {
            throw new NotFoundException("일치하는 사용자를 찾을 수 없습니다.");
        }
    }

    public Map<String, String> findId(String email) {
        Optional<Users> optionalUsers = userRepository.findByEmail(email);
        log.info("OptionalUsers: {}", optionalUsers);
        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();

            Map<String, String> map = new HashMap<>();
            map.put("uid", users.getUid());

            return map;
        } else {
            throw new NotFoundException("일치하는 사용자를 찾을 수 없습니다.");
        }
    }

    public Map<String, String> findEmail(String uid) {
        Optional<Users> optionalUsers = userRepository.findByUid(uid);
        if (optionalUsers.isPresent()) {
            Users users = optionalUsers.get();

            Map<String, String> map = new HashMap<>();
            map.put("email", users.getEmail());

            return map;
        } else {
            throw new NotFoundException("일치하는 사용자를 찾을 수 없습니다.");
        }
    }
}
