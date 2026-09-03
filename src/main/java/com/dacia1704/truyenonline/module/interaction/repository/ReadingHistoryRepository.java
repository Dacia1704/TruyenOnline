package com.dacia1704.truyenonline.module.interaction.repository;

import com.dacia1704.truyenonline.module.interaction.entity.HistoryType;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReadingHistoryRepository
        extends JpaRepository<ReadingHistory, String>, JpaSpecificationExecutor<ReadingHistory> {
    Optional<ReadingHistory> findByStoryIdAndUserIdAndType(
            String storyId, String userId, HistoryType type);

    Optional<ReadingHistory> findByStoryIdAndSessionIdAndType(
            String storyId, String sessionId, HistoryType type);

    @Transactional
    void deleteByUserId(String userId);

    @Transactional
    void deleteBySessionId(String sessionId);
}
