package com.dacia1704.truyenonline.module.administration.dto.response;

import com.dacia1704.truyenonline.module.administration.entity.ModerationActionType;
import com.dacia1704.truyenonline.module.administration.entity.ModerationObjectType;
import com.dacia1704.truyenonline.module.administration.entity.ViolationType;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.interaction.dto.response.CommentResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import java.time.LocalDateTime;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ModerationActionResponse {

    String id;

    String objectId;

    ModerationObjectType objectType;

    StoryResponse storyResponse;
    ChapterResponse chapterResponse;
    UserResponse userResponse;
    CommentResponse commentResponse;

    ModerationActionType actionType;

    ViolationType violationType;

    String reason;

    String adminId;

    String adminUsername;

    LocalDateTime createdAt;
}
