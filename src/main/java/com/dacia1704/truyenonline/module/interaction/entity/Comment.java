package com.dacia1704.truyenonline.module.interaction.entity;

import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "comments",
        indexes = {
            @Index(name = "idx_cmt_story_id", columnList = "story_id"),
            @Index(name = "idx_cmt_chapter_id", columnList = "chapter_id"),
            @Index(name = "idx_cmt_user_id", columnList = "user_id"),
        })
// Cập nhật lại Check Constraint để bắt buộc validate theo type
@Check(
        constraints =
                "(type = 'STORY' AND story_id IS NOT NULL) OR (type = 'CHAPTER' AND chapter_id IS"
                        + " NOT NULL)")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Comment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    User user;

    // Map thêm field type tương ứng với DB
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    CommentType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Chapter chapter;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    Comment parent;

    @Builder.Default // Thêm @Builder.Default để tránh null list khi build() object
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OrderBy("createdAt ASC")
    List<Comment> replies = new ArrayList<>();

    @Builder.Default
    @Column(name = "is_banned", nullable = false)
    Boolean isBanned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_moderation_id")
    ModerationAction currentModeration;
}
