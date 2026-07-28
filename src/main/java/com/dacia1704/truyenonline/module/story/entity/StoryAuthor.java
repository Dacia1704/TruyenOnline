package com.dacia1704.truyenonline.module.story.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "story_authors", indexes = {
        @Index(name = "idx_sa_author_id", columnList = "author_id")
})
@IdClass(StoryAuthorId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoryAuthor {

    // Khai báo 2 field Id riêng, đúng kiểu CHAR(36)
    @Id
    @Column(name = "story_id", columnDefinition = "CHAR(36)")
    private String storyId;

    @Id
    @Column(name = "author_id", columnDefinition = "CHAR(36)")
    private String authorId;

    // Relationship vẫn giữ nhưng insertable/updatable = false
    // vì 2 field trên đã quản lý cột rồi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", insertable = false, updatable = false)
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", insertable = false, updatable = false)
    private Author author;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 50)
    @Builder.Default
    private AuthorRole role = AuthorRole.AUTHOR;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Byte sortOrder = 1;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}