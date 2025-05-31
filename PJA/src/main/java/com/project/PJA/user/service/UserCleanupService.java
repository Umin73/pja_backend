package com.project.PJA.user.service;

import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserCleanupService {

    private UserRepository userRepository;

    @Scheduled(cron = "4 4 4 * * *") // 4(초) 4(분) 4(시) *(일) *(월) *(요일)
    public void deleteExpiredUnverifiedUsers() {
        List<Users> expiredUsers = userRepository.findAllByEmailVerifiedFalse();

        userRepository.deleteAll(expiredUsers);
    }
}
