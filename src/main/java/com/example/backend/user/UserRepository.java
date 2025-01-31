package com.example.backend.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
//    User findByUserId(String userId);
    User findByPhone(String phone);

    boolean existsByUserId(String userId);
    boolean existsByNickname(String nickname);
    boolean existsByKakaoUser(String kakaoUser);
    boolean existsByEmail(String email);
    Optional<User> findByuserId(String userId);
    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);
}