package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.request.AuthorCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.AuthorUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.AuthorResponse;
import com.dacia1704.truyenonline.module.story.dto.response.StoryAuthorResponse;
import com.dacia1704.truyenonline.module.story.entity.Author;
import com.dacia1704.truyenonline.module.story.entity.StoryAuthor;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        uses = {
                StoryMapper.class,
                AuthorMapper.class
        }
)
public interface StoryAuthorMapper {
    StoryAuthorResponse toStoryAuthorResponse(StoryAuthor storyAuthor);
}