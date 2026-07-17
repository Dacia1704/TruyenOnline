package com.dacia1704.truyenonline.module.story.dto.response;

import com.dacia1704.truyenonline.module.story.entity.StoryStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryType;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.entity.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryResponse {
    String id;

    UserResponse uploader;

    String title;

    String slug;

    String description;

    String coverImageUrl;

    StoryType storyType;

    StoryStatus status = StoryStatus.ONGOING;

    boolean isPublished;

    Integer freeChapterLimit;

    Long viewCount;
}
