package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.request.StoryCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryPublishRequestResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.entity.StoryPublishRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface StoryPublishRequestMapper {
    StoryPublishRequestResponse toStoryPublishRequestResponse(StoryPublishRequest storyPublishRequest);
}
