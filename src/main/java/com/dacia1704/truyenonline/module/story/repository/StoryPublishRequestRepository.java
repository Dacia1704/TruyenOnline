package com.dacia1704.truyenonline.module.story.repository;

import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryPublishRequestRepository
        extends JpaRepository<StoryPublishRequest, String>,
                JpaSpecificationExecutor<StoryPublishRequest> {}
