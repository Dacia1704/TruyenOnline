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
public class StoryCreateRequest {
    @NotEmpty(message = "Trường tiêu đề không được để trống")
    String title;

    String description;

    String coverImageUrl;

    @NotEmpty(message = "Trường thể loại truyện không được để trống")
    StoryType storyType;

    StoryStatus status = StoryStatus.ONGOING;

    boolean isPublished = false;

    Integer freeChapterLimit;

    Long viewCount = 0L;
}
