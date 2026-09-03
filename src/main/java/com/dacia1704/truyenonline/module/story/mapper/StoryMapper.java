package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.request.StoryCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {StoryAuthorMapper.class})
public interface StoryMapper {
    Story toStory(StoryResponse request);

    Story toStory(StoryCreateRequest request);

    Story toStory(StoryUpdateRequest request);

    StoryCreateRequest toStoryCreateRequest(Story story);

    StoryUpdateRequest toStoryUpdateRequest(Story story);

    StoryResponse toStoryResponse(Story story);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "authors", ignore = true)
    @Mapping(target = "genres", ignore = true)
    @Mapping(target = "coverImageUrl", ignore = true)
    void updateStory(@MappingTarget Story story, StoryUpdateRequest request);
}
