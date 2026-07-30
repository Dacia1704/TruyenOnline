package com.dacia1704.truyenonline.module.banner.dto.response;

import com.dacia1704.truyenonline.module.banner.entity.BannerPosition;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BannerResponse {

    private String id;

    private String title;

    private String bannerUrl;

    private String linkUrl;

    private BannerPosition position;

    private Integer sortOrder;

    private boolean isActive;

    private Long clickCount;

    private UserResponse createdBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}