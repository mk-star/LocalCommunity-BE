package com.example.backend.post;

import com.example.backend.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    //게시글 관련 repository
    Page<Post> findByTitleContainingOrContentContaining(String keywordTitle, String keywordContent, Pageable pageable);
    Page<Post> findByCategoryId(Long categoryId, Pageable pageable);
    Page<Post> findByCategoryIdAndTitleContainingOrContentContaining(Long categoryId, String keywordTitle, String keywordContent, Pageable pageable);
    Page<Post> findByUser(User user, Pageable pageable);
    long countByUser(User user);
    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND (p.title LIKE %:keyword% OR p.content LIKE %:keyword%)")
    Page<Post> findByCategoryIdAndKeyword(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);
    List<Post> findTop20ByViewGreaterThanOrderByViewDesc(int view);
    List<Post> findTop20ByLikeCountGreaterThanOrderByLikeCountDesc(int likeCount);
    List<Post> findTop20ByCommentCountGreaterThanOrderByCommentCountDesc(int commentCount);

    @Modifying
    @Query("UPDATE Post p SET p.user = :deletedUser WHERE p.user.id = :userId")
    void updatePostsToDeletedUser(@Param("userId") Long userId, @Param("deletedUser") User deletedUser);

}
