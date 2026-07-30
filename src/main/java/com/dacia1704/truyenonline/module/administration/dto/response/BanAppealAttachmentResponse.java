package com.dacia1704.truyenonline.module.administration.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanAppealAttachmentResponse {

    private String id;

    private String attachmentUrl;

    private LocalDateTime createdAt;
}