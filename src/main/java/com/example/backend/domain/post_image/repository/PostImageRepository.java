package com.example.backend.domain.post_image.repository;

import com.example.backend.domain.post_image.entity.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostImageRepository extends JpaRepository<PostImage, Long> {
    List<PostImage> findAllByFileUrlIn(List<String> fileUrls);
    @Modifying
    @Query("DELETE FROM PostImage pi WHERE pi.fileUrl = :fileUrl")
    void deleteByFileUrl(@Param("fileUrl") String fileUrl);

}