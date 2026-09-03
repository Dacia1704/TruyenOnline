package com.dacia1704.truyenonline.module.story.repository;

import com.dacia1704.truyenonline.module.story.entity.Genre;
import com.dacia1704.truyenonline.module.story.entity.Story;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository
        extends JpaRepository<Genre, Integer>, JpaSpecificationExecutor<Genre> {
    Optional<Story> findBySlug(String slug);

    boolean existsBySlug(String slug);
}
