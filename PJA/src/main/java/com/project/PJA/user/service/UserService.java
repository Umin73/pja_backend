package com.project.PJA.user.service;

import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.entity.UserRole;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

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
}
