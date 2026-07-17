package com.dacia1704.truyenonline.module.chapter.dto.request;

import com.dacia1704.truyenonline.module.administration.entity.ViolationType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterBanRequest {
    ViolationType violationType;

    @NotBlank
    @Size(max = 1000)
    String reason;
}
