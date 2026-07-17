package com.dacia1704.truyenonline.module.administration.dto.request;

import com.dacia1704.truyenonline.module.administration.entity.AuditAction;
import com.dacia1704.truyenonline.module.administration.entity.AuditObjectType;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuditLogCreateRequest {

    @NotNull
    AuditAction action;

    @NotNull
    AuditObjectType objectType;

    String objectId;

    String description;

    String oldValue;

    String newValue;
}