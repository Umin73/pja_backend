package com.project.PJA.security.service;

import com.project.PJA.common.dto.ErrorResponse;
import com.project.PJA.exception.NotFoundException;
import com.project.PJA.exception.UnauthorizedException;
import com.project.PJA.user.entity.UserStatus;
import com.project.PJA.user.entity.Users;
import com.project.PJA.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String uid) throws UsernameNotFoundException {

        Users user = userRepository.findByUid(uid)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 이메일 인증 후 활성화된 계정이 아닌 경우 예외 처리
        if(!user.isEmailVerified()) {
            throw new UnauthorizedException("이메일 인증이 완료되지 않았습니다.");
        }
        if(user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("비활성화된 계정입니다.");
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUid())
                .password(user.getPassword())
                .roles(user.getRole().name().replace("ROLE_", ""))
                .build();
    }
}
