package com.dacia1704.truyenonline.module.chapter.repository;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}
