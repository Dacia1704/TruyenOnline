package com.dacia1704.truyenonline.module.administration.dto.response;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogResponse {

    String id;

    String actorId;

    String actorUsername;

    String actorRole;

    AuditAction action;

    AuditObjectType objectType;

    String objectId;

    String description;

    String oldValue;

    String newValue;

    String ipAddress;

    String userAgent;

    LocalDateTime createdAt;
}
