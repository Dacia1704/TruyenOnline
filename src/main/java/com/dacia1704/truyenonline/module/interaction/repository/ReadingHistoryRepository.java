package com.dacia1704.truyenonline.module.interaction.repository;

import com.dacia1704.truyenonline.module.interaction.entity.HistoryType;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReadingHistoryRepository
        extends JpaRepository<ReadingHistory, String>, JpaSpecificationExecutor<ReadingHistory> {

    @Modifying
    @Query(value = """
    INSERT INTO reading_history
        (id, user_id, story_id, chapter_id, type, session_id, last_read_at, created_at, updated_at)
    VALUES
        (:id, :userId, :storyId, :chapterId, :type, NULL, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3))
    ON DUPLICATE KEY UPDATE
        chapter_id = VALUES(chapter_id),
        last_read_at = VALUES(last_read_at),
        updated_at = VALUES(updated_at)
    """, nativeQuery = true)
    void upsertByUser(
            @Param("id") String id,
            @Param("userId") String userId,
            @Param("storyId") String storyId,
            @Param("chapterId") String chapterId,
            @Param("type") String type);   // <-- đổi từ HistoryType sang String

    @Modifying
    @Query(value = """
    INSERT INTO reading_history
        (id, session_id, story_id, chapter_id, type, user_id, last_read_at, created_at, updated_at)
    VALUES
        (:id, :sessionId, :storyId, :chapterId, :type, NULL, CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3), CURRENT_TIMESTAMP(3))
    ON DUPLICATE KEY UPDATE
        chapter_id = VALUES(chapter_id),
        last_read_at = VALUES(last_read_at),
        updated_at = VALUES(updated_at)
    """, nativeQuery = true)
    void upsertByGuest(
            @Param("id") String id,
            @Param("sessionId") String sessionId,
            @Param("storyId") String storyId,
            @Param("chapterId") String chapterId,
            @Param("type") String type);   // <-- đổi từ HistoryType sang String

    Optional<ReadingHistory> findByStoryIdAndUserIdAndType(
            String storyId, String userId, HistoryType type);

    Optional<ReadingHistory> findByStoryIdAndSessionIdAndType(
            String storyId, String sessionId, HistoryType type);

    @Transactional
    void deleteByUserId(String userId);

    @Transactional
    void deleteByChapterId(String chapterId);

    @Transactional
    void deleteByStoryId(String storyId);

    @Transactional
    void deleteBySessionId(String sessionId);
}