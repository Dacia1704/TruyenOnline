package com.dacia1704.truyenonline.module.administration.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "ban_appeals",
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_appeal_moderation", columnNames = "moderation_action_id")
        },
        indexes = {
            @Index(name = "idx_appeal_user_id", columnList = "user_id"),
            @Index(name = "idx_appeal_status_created", columnList = "status, created_at")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BanAppeal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "moderation_action_id", nullable = false)
    ModerationAction moderationAction;

    @Column(columnDefinition = "TEXT", nullable = false)
    String content;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    BanAppealStatus status = BanAppealStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    User reviewer;

    @Column(name = "reviewer_note", columnDefinition = "TEXT")
    String reviewerNote;

    @Column(name = "resolved_at")
    LocalDateTime resolvedAt;

    @Builder.Default
    @OneToMany(mappedBy = "appeal", cascade = CascadeType.ALL, orphanRemoval = true)
    List<BanAppealAttachment> attachments = new ArrayList<>();
}
