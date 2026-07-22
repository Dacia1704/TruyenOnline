package com.dacia1704.truyenonline.module.interaction.dto.request;

import com.dacia1704.truyenonline.module.interaction.entity.HistoryType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Loại lịch sử không đuược để trống")
    HistoryType type;
}
