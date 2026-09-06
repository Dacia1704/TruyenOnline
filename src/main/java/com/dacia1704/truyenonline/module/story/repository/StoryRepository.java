package com.dacia1704.truyenonline.module.story.repository;

import com.dacia1704.truyenonline.module.story.entity.Story;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository
        extends JpaRepository<Story, String>, JpaSpecificationExecutor<Story> {
    Optional<Story> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Modifying
    @Query("UPDATE Story s SET s.viewCount = s.viewCount + 1 WHERE s.id = :storyId")
    void incrementViewCount(@Param("storyId") String storyId);

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM Story s
        WHERE s.id = :storyId
          AND s.uploader.id = :userId
    """)
        boolean isStoryUploader(
                @Param("storyId") String storyId,
                @Param("userId") String userId);
}
