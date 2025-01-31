package com.example.backend.comment_like;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    List<CommentLike> findByCommentId(Long postId);
    Optional<CommentLike> findByUserIdAndCommentId(Long userId, Long commentId);

    List<CommentLike> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE CommentLike  c SET c.user = :deletedUser WHERE c.user.id = :userId")
    void updateCommentLikeToDeletedUser(@Param("userId") Long userId, @Param("deletedUser") User deletedUser);
}
