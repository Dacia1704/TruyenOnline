package com.dacia1704.truyenonline.module.story.repository;

import com.dacia1704.truyenonline.module.story.entity.Author;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorRepository extends JpaRepository<Author, String> {
    Optional<Author> findBySlug(String slug);

    boolean existsBySlug(String slug);

    Page<Author> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
