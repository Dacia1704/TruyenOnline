package com.dacia1704.truyenonline.module.story.repository;

import com.dacia1704.truyenonline.module.story.entity.StoryAuthor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryAuthorRepository
        extends JpaRepository<StoryAuthor, String>, JpaSpecificationExecutor<StoryAuthor> {
    List<StoryAuthor> findByStoryId(String storyId);

    List<StoryAuthor> findByAuthorId(String authorId);

    void deleteByStoryId(String storyId);
}
