package com.dacia1704.truyenonline.module.administration.dto.response;

import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ViolationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModerationActionResponse {

    String id;

    String objectId;

    ModerationObjectType objectType;

    ModerationActionType actionType;

    ViolationType violationType;

    String reason;

    String adminId;

    String adminUsername;

    LocalDateTime createdAt;
}