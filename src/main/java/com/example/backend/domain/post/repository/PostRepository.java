package com.example.backend.domain.post.repository;

import com.example.backend.domain.post.entity.Post;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 비관적락 적용
    @Lock(LockModeType.PESSIMISTIC_WRITE) // 읽기 쓰기 잠금
    @Query("SELECT p FROM Post p WHERE p.id = :postId")
    Optional<Post> findByByWithPessimisticLock(@Param("postId") Long postId);
    // 낙관적락 적용
    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select p from Post p where p.id = :postId")
    Optional<Post> findByWithOptimisticLock(@Param("postId") Long postId);
}
