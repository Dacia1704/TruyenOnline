package com.dacia1704.truyenonline.module.chapter.entity;

import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "chapters",
        indexes = {
            @Index(name = "idx_chap_story_id", columnList = "story_id"),
        },
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_chapter",
                    columnNames = {"story_id", "chapter_number"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Chapter extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    // Mối quan hệ N-1 với bảng Story (Nhiều chapter thuộc về 1 truyện)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    Story story;

    @Column(name = "chapter_number", columnDefinition = "DECIMAL(8,1)", nullable = false)
    BigDecimal chapterNumber;

    @Column(name = "title", length = 500)
    String title;

    @Column(name = "title_no_accent", length = 500)
    String titleNoAccent;

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    boolean isPublished = false;

    @Builder.Default
    @Column(name = "view_count", nullable = false)
    Long viewCount = 0L;

    @Column(name = "content", columnDefinition = "LONGTEXT")
    String content;

    @Column(name = "page_count")
    Integer pageCount;
}
