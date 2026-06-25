package com.dacia1704.truyenonline.module.interaction.entity;


import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "chapters",
        indexes = {
                @Index(name = "idx_rh_user_id", columnList = "user_id"),
                @Index(name = "idx_rh_session_id", columnList = "session_id"),
                @Index(name = "idx_rh_last_read", columnList = "last_read_at"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_user_story", columnNames = {"user_id", "story_id"}),
                @UniqueConstraint(name = "uq_session_story", columnNames = {"session_id", "story_id"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Column(name = "session_id")
    String sessionId;

    @Column(name = "last_read_at", nullable = false, updatable = false)
    LocalDateTime lastReadAt;
}
