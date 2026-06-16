package com.dacia1704.truyenonline.module.story.dto.request;

import com.dacia1704.truyenonline.module.story.entity.StoryStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryType;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryUpdateRequest {
    String title;

    String description;

    String coverImageUrl;

    StoryType storyType;

    StoryStatus status;

    boolean isPublished = false;

    Integer freeChapterLimit;

    Long viewCount = 0L;
}
