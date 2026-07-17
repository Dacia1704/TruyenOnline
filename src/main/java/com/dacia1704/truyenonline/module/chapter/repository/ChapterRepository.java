package com.dacia1704.truyenonline.module.chapter.repository;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChapterRepository
        extends JpaRepository<Chapter, String>, JpaSpecificationExecutor<Chapter> {

}
