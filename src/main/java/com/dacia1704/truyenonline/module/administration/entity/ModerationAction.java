package com.dacia1704.truyenonline.module.administration.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "moderation_actions",
        indexes = {
            @Index(name = "idx_moderation_object", columnList = "object_type, object_id"),
            @Index(name = "idx_moderation_admin", columnList = "admin_id"),
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModerationAction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @Column(name = "object_id", columnDefinition = "CHAR(36)", nullable = false)
    String objectId;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false)
    ModerationObjectType objectType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    User admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    ModerationActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type")
    ViolationType violationType;

    @Column(name = "reason", columnDefinition = "TEXT")
    String reason;
}
