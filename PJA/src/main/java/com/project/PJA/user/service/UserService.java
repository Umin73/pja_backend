package com.project.PJA.user.service;

import com.project.PJA.exception.NotFoundException;
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

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public boolean signup(SignupDto signupDto) {

        Users user = new Users();
        user.setUid(signupDto.getUid());
        user.setName(signupDto.getName());
        user.setPassword(passwordEncoder.encode(signupDto.getPassword()));
        user.setEmail(signupDto.getEmail());
        user.setRole(UserRole.ROLE_USER);

        userRepository.save(user);

        return true;
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
