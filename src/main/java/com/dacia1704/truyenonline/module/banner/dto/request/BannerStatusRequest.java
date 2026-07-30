package com.dacia1704.truyenonline.module.banner.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerStatusRequest {

    @NotNull
    private Boolean active;
}