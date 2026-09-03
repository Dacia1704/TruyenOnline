package com.dacia1704.truyenonline.module.chapter.mapper;

import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterCreateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterUpdateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
            StoryMapper.class,
        })
public interface ChapterMapper {
    @Mapping(target = "content", ignore = true)
    ChapterResponse toChapterResponse(Chapter chapter);

    Chapter toChapter(ChapterCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateChapter(@MappingTarget Chapter chapter, ChapterUpdateRequest request);
}
