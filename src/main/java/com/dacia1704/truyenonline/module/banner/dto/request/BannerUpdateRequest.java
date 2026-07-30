package com.dacia1704.truyenonline.module.banner.dto.request;

import com.dacia1704.truyenonline.module.banner.entity.BannerPosition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerUpdateRequest {

    @NotBlank
    private String title;

    private String linkUrl;

    private String bannerUrl;

    private MultipartFile imageFile;

    @NotNull
    private BannerPosition position;

    @NotNull
    private Integer sortOrder;
}