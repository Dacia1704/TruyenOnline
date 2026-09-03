package com.dacia1704.truyenonline.module.authentication.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rt_user"))
    private User user;

    @Column(name = "token_hash", length = 255, nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "device_name", length = 255)
    private String deviceName;

    @Column(name = "device_id", length = 255)
    private String deviceId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    @Column(name = "expired_at", nullable = false)
    private LocalDateTime expiredAt;

    // NULL = token còn hiệu lực. Set khi logout (thiết bị này) hoặc logout-all-devices.
    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    // Cập nhật mỗi lần token này được dùng để cấp access token mới.
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Token còn hiệu lực để cấp access token mới hay không.

    @Transient
    public boolean isActive() {
        return revokedAt == null && expiredAt != null && expiredAt.isAfter(LocalDateTime.now());
    }
}
