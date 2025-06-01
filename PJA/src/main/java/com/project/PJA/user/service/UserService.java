package com.project.PJA.user.service;

import ch.qos.logback.core.subst.Token;
import com.project.PJA.email.service.EmailServiceImpl;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.security.service.EmailVerificationService;
import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.entity.UserRole;
import com.project.PJA.user.entity.UserStatus;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailServiceImpl emailServiceImpl;

    public boolean signup(SignupDto signupDto) {

        if(userRepository.existsByUid(signupDto.getUid())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }
        if(userRepository.existsByEmail(signupDto.getEmail())) {
            throw new RuntimeException("이미 등록된 이메일입니다.");
        }

        Users user = new Users();
        user.setUid(signupDto.getUid());
        user.setName(signupDto.getName());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setEmail(signupDto.getEmail());
        user.setRole(UserRole.ROLE_USER);
        user.setStatus(UserStatus.UNVERIFIED);
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

            // 6자리 숫자 토큰 생성
            String token = String.format("%06d", new Random().nextInt(1000000));
            log.info("token: {}", token);

            emailVerificationService.saveEmailVerificationToken(user.getEmail(), token, 60*24); // 토큰 24시간동안 유효함

            // 이메일 전송
            emailServiceImpl.sendSignupEmail(email, token);

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
            user.setStatus(UserStatus.ACTIVE);
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

    @Transactional
    public void withdraw(String uid) {
        Optional<Users> optionalUser = userRepository.findByUid(uid);
        log.info("optionalUser: {}", optionalUser);

        if(optionalUser.isPresent()) {
            Users user = optionalUser.get();

            user.setStatus(UserStatus.WITHDRAW);
            user.setUid(null);
            user.setEmail(null);
            user.setPassword(null);
            user.setName("탈퇴한 사용자");
            user.setProfileImage(null);

            userRepository.save(user);
        } else {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public void updateUserName(String uid, String newName) {
        Optional<Users> optionalUsers = userRepository.findByUid(uid);
        if(optionalUsers.isEmpty()) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        Users user = optionalUsers.get();
        user.setName(newName);
        userRepository.save(user);
    }
}
