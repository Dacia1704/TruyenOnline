package com.dacia1704.truyenonline.module.administration.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanAppealRejectRequest {

    @NotBlank
    private String reviewerNote;
}