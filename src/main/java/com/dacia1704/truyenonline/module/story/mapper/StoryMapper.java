package com.dacia1704.truyenonline.module.story.mapper;
import com.dacia1704.truyenonline.module.story.dto.request.StoryCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.dto.request.UserUpdateRequest;
import com.dacia1704.truyenonline.module.user.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface StoryMapper {
    Story toStory(StoryResponse request);
    Story toStory(StoryCreateRequest request);
    Story toStory(StoryUpdateRequest request);

    StoryCreateRequest toStoryCreateRequest(Story story);
    StoryUpdateRequest toStoryUpdateRequest(Story story);
    StoryResponse toStoryResponse(Story story);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateStory(@MappingTarget Story story, StoryUpdateRequest request);
}
