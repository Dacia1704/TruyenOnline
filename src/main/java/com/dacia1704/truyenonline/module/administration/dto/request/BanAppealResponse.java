package com.dacia1704.truyenonline.module.administration.dto.request;

import com.dacia1704.truyenonline.module.administration.dto.response.BanAppealAttachmentResponse;
import com.dacia1704.truyenonline.module.administration.dto.response.ModerationActionResponse;
import com.dacia1704.truyenonline.module.administration.entity.BanAppealStatus;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BanAppealResponse {

    private String id;

    private UserResponse user;

    private ModerationActionResponse moderationAction;

    private String content;

    private BanAppealStatus status;

    private UserResponse reviewer;

    private String reviewerNote;

    private LocalDateTime resolvedAt;

    private List<BanAppealAttachmentResponse> attachments;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}