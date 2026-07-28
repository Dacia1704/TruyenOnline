package com.dacia1704.truyenonline.module.authentication.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "user_social_accounts",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"provider", "provider_user_id"})})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSocialAccount extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    AuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    String providerUserId;
}
