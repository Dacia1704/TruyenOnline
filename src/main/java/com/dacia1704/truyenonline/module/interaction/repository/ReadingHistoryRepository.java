package com.dacia1704.truyenonline.module.interaction.repository;

import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ReadingHistoryRepository extends JpaRepository<ReadingHistory, String> {
    Optional<ReadingHistory> findByStoryIdAndUserId(String storyId, String userId);

    Optional<ReadingHistory> findByStoryIdAndSessionId(String storyId, String sessionId);

    Page<ReadingHistory> findByUserId(String userId, Pageable pageable);

    Page<ReadingHistory> findBySessionId(String sessionId, Pageable pageable);

    @Transactional
    long deleteByUserId(String userId);

    @Transactional
    long deleteBySessionId(String sessionId);
}
