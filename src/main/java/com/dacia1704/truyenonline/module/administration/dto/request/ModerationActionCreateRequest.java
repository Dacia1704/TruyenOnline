package com.dacia1704.truyenonline.module.administration.dto.request;

import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ViolationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModerationActionCreateRequest {
    @NotBlank
    String objectId;

    @NotNull
    ModerationObjectType objectType;

    @NotNull
    ModerationActionType actionType;

    ViolationType violationType;

    @NotBlank
    @Size(max = 1000)
    String reason;
}
