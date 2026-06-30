package com.dacia1704.truyenonline.module.interaction.repository;

import com.dacia1704.truyenonline.module.interaction.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    Page<Comment> findByChapterIdAndParentIsNull(String chapterId, Pageable pageable);
}
