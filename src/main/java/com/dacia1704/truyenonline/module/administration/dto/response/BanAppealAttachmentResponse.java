package com.dacia1704.truyenonline.module.administration.dto.response;

import java.time.LocalDateTime;
import lombok.*;

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
