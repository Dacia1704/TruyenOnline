package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.response.StoryAuthorResponse;
import com.dacia1704.truyenonline.module.story.entity.StoryAuthor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {StoryMapper.class, AuthorMapper.class})
public interface StoryAuthorMapper {
    @Mapping(target = "author", source = "author")
    @Mapping(target = "story", source = "story")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "sortOrder", source = "sortOrder")
    StoryAuthorResponse toStoryAuthorResponse(StoryAuthor storyAuthor);
}
