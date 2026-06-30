package com.dacia1704.truyenonline.module.interaction.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistoryRequest {
    @NotBlank(message = "Chapter ID không được để trống")
    String chapterId;

    @NotBlank(message = "Story ID không được để trống")
    String storyId;
}
