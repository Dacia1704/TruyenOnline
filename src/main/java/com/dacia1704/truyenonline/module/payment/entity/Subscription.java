package com.dacia1704.truyenonline.module.payment.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "subscriptions",
        indexes = {
            @Index(name = "idx_sub_user_id", columnList = "user_id"),
            @Index(name = "idx_sub_expires_at", columnList = "expires_at"),
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Subscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    SubscriptionStatus status;

    @Column(name = "started_at", nullable = false)
    LocalDateTime startedAt;

    @Column(name = "expires_at", nullable = false)
    LocalDateTime expiresAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "plan",
            referencedColumnName = "code",
            foreignKey = @ForeignKey(name = "fk_sub_plan")
    )
    private SubscriptionPlan plan;

}
