package com.example.backend.category;

import com.example.backend.post.Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "SELECT p FROM Post p WHERE p.category.id = :categoryId ORDER BY p.createdDate DESC")
    List<Post> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.view > :view ORDER BY p.view DESC")
    List<Post> findTop2ByView(@Param("view") int view, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.likeCount > :likeCount ORDER BY p.likeCount DESC")
    List<Post> findTop2ByLikeCount(@Param("likeCount") int likeCount, Pageable pageable);
    @Query("SELECT p FROM Post p WHERE p.commentCount > :commentCount ORDER BY p.commentCount DESC")
    List<Post> findTop2ByCommentCount(@Param("commentCount") int commentCount, Pageable pageable);
}

