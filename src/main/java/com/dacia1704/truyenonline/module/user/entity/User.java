package com.dacia1704.truyenonline.module.user.entity;

import com.dacia1704.truyenonline.module.administration.entity.ModerationAction;
import com.dacia1704.truyenonline.module.authentication.entity.UserSocialAccount;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @Column(name = "email", nullable = false, unique = true)
    String email;

    @Column(name = "username", nullable = false, unique = true)
    String username;

    @Column(name = "password_hash")
    String passwordHash;

    @Column(name = "avatar_url", length = 500)
    String avatarUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    boolean isActive = true;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles", // Tên bảng trung gian trong DB
            joinColumns = @JoinColumn(name = "user_id"), // Khóa ngoại trỏ tới bảng users (CHAR 36)
            inverseJoinColumns =
                    @JoinColumn(name = "role_id") // Khóa ngoại trỏ tới bảng roles (INT)
            )
    Set<Role> roles = new HashSet<>();

    @Builder.Default
    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY,
            orphanRemoval = true)
    Set<UserSocialAccount> socialAccounts = new HashSet<>();

    @Builder.Default
    @Column(name = "is_banned", nullable = false)
    Boolean isBanned = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_moderation_id")
    ModerationAction currentModeration;
}
