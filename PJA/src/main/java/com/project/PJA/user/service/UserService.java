package com.project.PJA.user.service;

import com.project.PJA.common.file.FileStorageService;
import com.project.PJA.email.service.EmailServiceImpl;
import com.project.PJA.exception.BadRequestException;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.user.dto.*;
import com.project.PJA.security.service.EmailVerificationService;
import com.project.PJA.user.entity.UserRole;
import com.project.PJA.user.entity.UserStatus;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationService emailVerificationService;
    private final EmailServiceImpl emailServiceImpl;
    private final FileStorageService fileStorageService;

    @Value("${FILE_UPLOAD_DIR}")
    private String uploadDir;

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
    @Transactional
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

    // 비밀번호 찾기 인증번호 보내기
    public void sendFindPwEmail(IdEmailRequestDto dto) {

        log.info("dto.getUid: {}", dto.getUid());
        log.info("dto.getEmail: {}", dto.getEmail());

        Optional<Users> optionalUser = userRepository.findByUid(dto.getUid());
        log.info("optionalUser: {}", optionalUser);
        if(optionalUser.isPresent()) {
            Users user = optionalUser.get();
            log.info("user: {}", user);

            if(!Objects.equals(user.getEmail(), dto.getEmail())) {
                throw new BadRequestException("계정에 등록된 이메일과 입력하신 이메일이 일치하지 않습니다.");
            }

            // 6자리 숫자 토큰 생성
            String token = String.format("%06d", new Random().nextInt(1000000));
            log.info("token: {}", token);

            emailVerificationService.saveFindPwVerificationToken(user.getEmail(), token, 60*24); // 토큰 24시간동안 유효함

            // 이메일 전송
            emailServiceImpl.sendFindPwEmail(dto.getEmail(), token);

        } else {
            throw new NotFoundException("일치하는 사용자를 찾을 수 없습니다.");
        }
    }

    public void verifyFindPwCode(VerifyEmailRequestDto dto) {
        Optional<Users> optionalUser = userRepository.findByEmail(dto.getEmail());

        if(optionalUser.isPresent()) {
            Users user = optionalUser.get();

            String storedToken = emailVerificationService.getFindPwVerificationToken(user.getEmail());
            if(storedToken == null || !storedToken.equals(dto.getToken())) {
                throw new UnauthorizedException("유효하지 않거나 만료된 인증 토큰 입니다.");
            }

            // 인증된 이메일 토큰은 삭제
            emailVerificationService.deleteFindPwVerificationToken(dto.getEmail());
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
    public void updateUserName(Users user, String newName) {
        if(user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        user.setName(newName);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword2(ChangePw2RequestDto dto) {
        Optional<Users> optionalUsers = userRepository.findByUid(dto.getUid());

        if(optionalUsers.isEmpty()) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }

        Users user = optionalUsers.get();

        // 새 비밀번호 유효성 체크
        String newPw = dto.getNewPw();
        if (!newPw.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$")) {
            throw new BadRequestException("비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.");
        }

        // 새 비밀번호 != 확인 비밀번호 검사
        if(!newPw.equals(dto.getConfirmPw())) {
            throw new BadRequestException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(Users user, ChangePwRequestDto dto) {

        if(user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 현재 비밀번호 일치 확인
        if(!passwordEncoder.matches(dto.getCurrentPw(), user.getPassword())) {
            throw new UnauthorizedException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 유효성 체크
        String newPw = dto.getNewPw();
        if (!newPw.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,16}$")) {
            throw new BadRequestException("비밀번호는 8~16자의 영문, 숫자, 특수문자를 포함해야 합니다.");
        }

        // 새 비밀번호 == 현재 비밀번호 검사
        if(passwordEncoder.matches(newPw, user.getPassword())) {
            throw new BadRequestException("새 비밀번호가 이전 비밀번호화 일치합니다.");
        }

        // 새 비밀번호 != 확인 비밀번호 검사
        if(!newPw.equals(dto.getConfirmPw())) {
            throw new BadRequestException("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(passwordEncoder.encode(newPw));
        userRepository.save(user);
    }

    @Transactional
    public void updateProfileImage(Users user, MultipartFile file) throws IOException {

        if(user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 프로필 이미지가 있으면 삭제
        String oldPath = user.getProfileImage();
        if (oldPath != null && !oldPath.isBlank()) {
            String fileName = Paths.get(oldPath).getFileName().toString();
            Path fullPath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath();
            Files.deleteIfExists(fullPath);
        }

        String newImagePath = fileStorageService.storeFile(file, user.getUserId());
        user.setProfileImage(newImagePath);
    }

    @Transactional
    public void deleteProfileImage(Users user) throws IOException {

        if (user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        String imagePath = user.getProfileImage();

        if(imagePath != null && !imagePath.isBlank()) {
            String fileName = Paths.get(imagePath).getFileName().toString();
            Path fullPath = Paths.get(uploadDir).resolve(fileName).toAbsolutePath();

            Files.deleteIfExists(fullPath);
        }

        user.setProfileImage(null);
    }

    public Map<String, Object> getUserInfo(Users user) {

        if(user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("username", user.getUsername());
        userInfo.put("profileImage", user.getProfileImage());

        return userInfo;
    }
}
