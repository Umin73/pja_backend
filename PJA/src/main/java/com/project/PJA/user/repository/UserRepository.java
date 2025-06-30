package com.project.PJA.user.repository;

import com.project.PJA.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("select u.userId from Users u where u.uid = :uid")
    Long findUserIdByUid(@Param("uid") String uid);

    Boolean existsByUid(String uid);
    Boolean existsByEmail(String email);

    Optional<Users> findByUid(String uid);
    Optional<Users> findByEmail(String email);

    List<Users> findAllByEmailVerifiedFalse();
}
