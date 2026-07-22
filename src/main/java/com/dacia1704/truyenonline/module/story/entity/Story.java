package com.dacia1704.truyenonline.module.story.entity;

import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.interaction.entity.Bookmark;
import com.dacia1704.truyenonline.module.user.entity.User; // Import Entity User của bạn
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "stories",
        indexes = {
            @Index(name = "idx_story_slug", columnList = "slug"),
            @Index(name = "idx_story_uploader_id", columnList = "uploader_id"),
            @Index(name = "idx_story_status", columnList = "status"),
            @Index(name = "idx_story_view_count", columnList = "view_count")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Story extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    // Chuyển từ String uploaderId sang Mối quan hệ đối tượng chuẩn JPA
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    User uploader;

    @Column(name = "title", nullable = false, length = 500)
    String title;

    @Column(name = "title_no_accent", nullable = false, length = 500)
    String titleNoAccent;

    @Column(name = "slug", nullable = false, unique = true, length = 500)
    String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "cover_image_url", length = 500)
    String coverImageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "story_type", nullable = false)
    StoryType storyType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    StoryStatus status = StoryStatus.ONGOING; // Khớp với DEFAULT 'ONGOING' của DB

    @Builder.Default
    @Column(name = "is_published", nullable = false)
    boolean isPublished = false;

    // Sửa thành Integer để chấp nhận giá trị NULL (Toàn bộ free)
    @Column(name = "free_chapter_limit")
    Integer freeChapterLimit;

    // Sửa thành Long để khớp với kiểu BIGINT dưới DB
    @Builder.Default
    @Column(name = "view_count", nullable = false)
    Long viewCount = 0L;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "story_genres",
            joinColumns = @JoinColumn(name = "story_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id"))
    Set<Genre> genres = new HashSet<>();

    @Builder.Default
    @Column(name = "is_banned", nullable = false)
    boolean isBanned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_moderation_id")
    ModerationAction currentModeration;

    @OneToMany(mappedBy = "story")
    Set<Bookmark> bookmarks = new HashSet<>();
}
