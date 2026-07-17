package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequest;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface StoryPublishRequestMapper {
    StoryPublishRequestResponse toStoryPublishRequestResponse(
            StoryPublishRequest storyPublishRequest);

    @Mapping(target = "roles", ignore = true)
    UserResponse toUserResponse(User user);

    StoryResponse toStoryResponse(Story story);
}
