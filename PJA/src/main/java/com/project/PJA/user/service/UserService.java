package com.project.PJA.user.service;

import com.project.PJA.user.dto.SignupDto;
import com.project.PJA.user.entity.UserRole;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean signup(SignupDto signupDto) {

        Users user = new Users();
        user.setUid(signupDto.getUid());
        user.setName(signupDto.getName());
        user.setPassword(signupDto.getPassword());
        user.setEmail(signupDto.getEmail());
        user.setRole(UserRole.ROLE_USER);

        userRepository.save(user);

        return true;
    }
}
