package com.example.backend.region;

import com.example.backend.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {
    List<Region> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Region r SET r.user = :deletedUser WHERE r.user.id = :userId")
    void updateRegionToDeletedUser(@Param("userId") Long userId, @Param("deletedUser") User deletedUser);
}
