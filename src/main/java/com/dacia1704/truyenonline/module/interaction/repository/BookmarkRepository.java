package com.dacia1704.truyenonline.module.interaction.repository;

import com.dacia1704.truyenonline.module.interaction.entity.Bookmark;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, String> {
    Optional<Bookmark> findByUserIdAndStoryId(String userId, String storyId);

    @Transactional
    long deleteByUserIdAndStoryId(String userId, String storyId);
}
