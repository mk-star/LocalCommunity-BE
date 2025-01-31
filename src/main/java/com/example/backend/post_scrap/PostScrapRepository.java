package com.example.backend.post_scrap;

import com.example.backend.user.User;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostScrapRepository extends JpaRepository<PostScrap, Long> {
    List<PostScrap> findByPostId(Long postId);
    Optional<PostScrap> findByUserIdAndPostId(Long userId, Long postId);
    @Transactional
    void deleteByUserIdAndPostId(Long userId, Long postId);

    Page<PostScrap> findByUser(User user, Pageable pageable);
    long countByUser(User user);
    @Modifying
    @Query("UPDATE PostScrap p SET p.user = :deletedUser WHERE p.user.id = :userId")
    void updatePostScrapToDeletedUser(@Param("userId") Long userId, @Param("deletedUser") User deletedUser);

}
