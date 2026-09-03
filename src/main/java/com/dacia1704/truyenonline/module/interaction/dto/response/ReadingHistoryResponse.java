package com.dacia1704.truyenonline.module.interaction.dto.response;

import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ReadingHistoryResponse {
    String id;
    StoryResponse story;
    ChapterResponse chapter;
    LocalDateTime lastReadAt;
}
