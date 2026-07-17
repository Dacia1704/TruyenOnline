package com.dacia1704.truyenonline.module.administration.dto.request;

import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ViolationType;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModerationActionPageRequest {

    @Builder.Default
    Integer page = 1;

    @Builder.Default
    Integer size = 10;

    String objectId;

    ModerationObjectType objectType;

    ModerationActionType actionType;

    ViolationType violationType;
}