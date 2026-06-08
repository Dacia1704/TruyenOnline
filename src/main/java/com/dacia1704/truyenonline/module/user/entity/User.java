package com.dacia1704.truyenonline.module.user.entity;

import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.HashSet;
import java.util.Set;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false)
    AuthProvider authProvider;

    @Column(name = "google_id", unique = true)
    String googleId;

    @Column(name = "is_active", nullable = false)
    boolean isActive;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles", // Tên bảng trung gian trong DB
            joinColumns = @JoinColumn(name = "user_id"), // Khóa ngoại trỏ tới bảng users (CHAR 36)
            inverseJoinColumns = @JoinColumn(name = "role_id") // Khóa ngoại trỏ tới bảng roles (INT)
    )
    Set<Role> roles = new HashSet<>();
}
