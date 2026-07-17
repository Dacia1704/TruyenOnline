package com.dacia1704.truyenonline.module.administration.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_logs_actor", columnList = "actor_id"),
                @Index(name = "idx_audit_logs_object", columnList = "object_type, object_id"),
                @Index(name = "idx_audit_logs_action", columnList = "action"),
                @Index(name = "idx_audit_logs_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    User actor;

    @Column(name = "actor_role", nullable = false, length = 100)
    String actorRole;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    AuditAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "object_type", nullable = false)
    AuditObjectType objectType;

    @Column(name = "object_id", columnDefinition = "CHAR(36)")
    String objectId;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "old_value", columnDefinition = "JSON")
    String oldValue;

    @Column(name = "new_value", columnDefinition = "JSON")
    String newValue;

    @Column(name = "ip_address", length = 45)
    String ipAddress;

    @Column(name = "user_agent", length = 255) // trình duyệt thiết bị
    String userAgent;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}