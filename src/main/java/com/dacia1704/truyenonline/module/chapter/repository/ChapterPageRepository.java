package com.dacia1704.truyenonline.module.chapter.repository;

import com.dacia1704.truyenonline.module.chapter.entity.ChapterPage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChapterPageRepository
        extends JpaRepository<ChapterPage, String>, JpaSpecificationExecutor<ChapterPage> {
    List<ChapterPage> findByChapterId(String chapterId);

    List<ChapterPage> findByChapterIdOrderByPageNumberAsc(String chapterId);

    @Modifying
    @Query("DELETE FROM ChapterPage c WHERE c.chapter.id = :chapterId")
    void deleteByChapterId(@Param("chapterId") String chapterId);
}
