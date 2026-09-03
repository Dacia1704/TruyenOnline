package com.dacia1704.truyenonline.module.banner.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "banners",
        indexes = {
            @Index(
                    name = "idx_banner_position_active",
                    columnList = "position, is_active, sort_order"),
            @Index(name = "idx_banner_created_by", columnList = "created_by")
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Banner extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    String id;

    @Column(nullable = false, length = 255)
    String title;

    @Column(name = "banner_url", nullable = false, length = 500)
    String bannerUrl;

    @Column(name = "link_url", length = 500)
    String linkUrl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    BannerPosition position = BannerPosition.HOME_HERO;

    @Builder.Default
    @Column(name = "sort_order", nullable = false)
    Integer sortOrder = 1;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    Boolean isActive = true;

    @Builder.Default
    @Column(name = "click_count", nullable = false)
    Long clickCount = 0L;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    User createdBy;
}
