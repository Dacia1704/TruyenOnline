package com.dacia1704.truyenonline.module.payment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlan {

    // PK là code VARCHAR khớp thẳng với enum SubscriptionPlan
    // VD: "PREMIUM_1M", "PREMIUM_3M", "PREMIUM_1Y"
    @Id
    @Column(name = "code", length = 20)
    private String code;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Giá VND — chưa nhân 100 (VNPay mới nhân)
    @Column(name = "price", nullable = false)
    private Long price;

    // Số ngày hiệu lực sau khi kích hoạt
    @Column(name = "duration_days", nullable = false)
    private Integer durationDays;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // Thứ tự hiển thị trên UI (1 = hiển thị trước)
    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Byte sortOrder = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
}
