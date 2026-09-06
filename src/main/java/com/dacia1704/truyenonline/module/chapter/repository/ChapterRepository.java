package com.dacia1704.truyenonline.module.chapter.repository;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChapterRepository
        extends JpaRepository<Chapter, String>, JpaSpecificationExecutor<Chapter> {
    long countByStory_Id(String storyId);

    @Query(
            """
                SELECT MAX(c.chapterNumber)
                FROM Chapter c
                WHERE c.story.id = :storyId
            """)
    BigDecimal findMaxChapterNumberByStoryId(@Param("storyId") String storyId);

    List<Chapter> findByStoryId(String storyId);

    @Modifying
    @Query("UPDATE Chapter c SET c.viewCount = c.viewCount + 1 WHERE c.id = :chapterId")
    void incrementViewCount(@Param("chapterId") String chapterId);

    @Query("""
        SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END
        FROM Chapter c
        WHERE c.id = :chapterId
          AND c.story.uploader.id = :userId
    """)
        boolean isChapterUploader(
                @Param("chapterId") String chapterId,
                @Param("userId") String userId);
}
