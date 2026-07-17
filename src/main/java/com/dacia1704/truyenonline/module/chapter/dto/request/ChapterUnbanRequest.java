package com.dacia1704.truyenonline.module.chapter.dto.request;

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
public class ChapterUnbanRequest {
    @NotBlank
    @Size(max = 1000)
    String reason;
}
