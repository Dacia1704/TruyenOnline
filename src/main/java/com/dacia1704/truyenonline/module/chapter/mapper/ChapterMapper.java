package com.dacia1704.truyenonline.module.chapter.mapper;

import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterCreateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterUpdateRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.dto.request.StoryCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.StoryUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ChapterMapper {
    ChapterResponse toChapterResponse(Chapter chapter);

    Chapter toChapter(ChapterCreateRequest request);
    Chapter toChapter(ChapterUpdateRequest request);

    void updateChapter(@MappingTarget Chapter chapter, ChapterUpdateRequest request);
}
