package com.dacia1704.truyenonline.module.administration.dto.request;

import com.dacia1704.truyenonline.module.administration.entity.*;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogPageRequest {

    @Builder.Default Integer page = 1;

    @Builder.Default Integer size = 10;

    String actorId;

    AuditAction action;

    AuditObjectType objectType;

    String objectId;

    LocalDateTime fromDate;

    LocalDateTime toDate;
}
