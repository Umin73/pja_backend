package com.project.PJA.user.repository;

import com.project.PJA.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByUid(String uid);
    Optional<Users> findByEmail(String email);

    List<Users> findAllByEmailVerifiedFalse();
}
