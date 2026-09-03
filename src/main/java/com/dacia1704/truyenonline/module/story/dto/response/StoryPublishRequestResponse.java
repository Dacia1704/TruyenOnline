package com.dacia1704.truyenonline.module.story.dto.response;

import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequestStatus;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryPublishRequestResponse {
    String id;
    StoryResponse story;
    String requesterNote;
    String reviewerNote;
    UserResponse reviewer;
    StoryPublishRequestStatus status;
}
