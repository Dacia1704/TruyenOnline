package com.dacia1704.truyenonline.module.story.repository;

import com.dacia1704.truyenonline.module.story.entity.Author;
import com.dacia1704.truyenonline.module.story.entity.StoryAuthor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoryAuthorRepository extends JpaRepository<StoryAuthor, String>, JpaSpecificationExecutor<StoryAuthor> {
    List<StoryAuthor> findByStoryId(String storyId);
    void deleteByStoryId(String storyId);
}
