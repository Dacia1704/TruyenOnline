package com.dacia1704.truyenonline.module.interaction.entity;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "reading_history",
        indexes = {
            @Index(name = "idx_rh_user_id", columnList = "user_id"),
            @Index(name = "idx_rh_session_id", columnList = "session_id"),
            @Index(name = "idx_rh_last_read", columnList = "last_read_at"),
        },
        // Cập nhật lại Unique Constraint thêm cột 'type'
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uq_user_story_type",
                    columnNames = {"user_id", "story_id", "type"}),
            @UniqueConstraint(
                    name = "uq_session_story_type",
                    columnNames = {"session_id", "story_id", "type"})
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
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "session_id")
    String sessionId;

    // Thêm trường type phân loại lịch sử đọc
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    HistoryType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    Story story;

    // Bỏ nullable = false để cho phép lưu lịch sử theo Story (không cần chapter_id)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    Chapter chapter;

    @Column(name = "last_read_at", nullable = false)
    LocalDateTime lastReadAt;

    @PrePersist
    @PreUpdate
    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }
}
