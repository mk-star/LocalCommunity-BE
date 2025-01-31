package com.example.backend.comment;

import com.example.backend.post.Post;
import com.example.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostIdAndParentCommentIsNull(Long postId);
    List<Comment> findByParentCommentId(Long parentCommentId);
    List<Comment> findByPostId(Long postId);

    Page<Comment> findByUser(User user, Pageable pageable);
    List<Comment> findByUser(User user);
    long countByUser(User user);

    @Modifying
    @Query("UPDATE Comment c SET c.user = :deletedUser WHERE c.user.id = :userId")
    void updateCommentsToDeletedUser(@Param("userId") Long userId, @Param("deletedUser") User deletedUser);


}
