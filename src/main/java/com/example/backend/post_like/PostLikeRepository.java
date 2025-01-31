package com.example.backend.post_like;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    List<PostLike> findByPostId(Long postId);

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    List<PostLike> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE PostLike p SET p.user = :deletedUser WHERE p.user.id = :userId")
    void updatePostLikeToDeletedUser(@Param("userId") Long userId, @Param("deletedUser") User deletedUser);
}
