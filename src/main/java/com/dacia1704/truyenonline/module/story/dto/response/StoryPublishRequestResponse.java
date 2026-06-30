package com.dacia1704.truyenonline.module.story.dto.response;

import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequestStatus;
import com.dacia1704.truyenonline.module.user.entity.User;

public class StoryPublishRequestResponse {
    String id;
    Story story;
    String requesterNote;
    String reviewerNote;
    User reviewer;
    StoryPublishRequestStatus status;
}
