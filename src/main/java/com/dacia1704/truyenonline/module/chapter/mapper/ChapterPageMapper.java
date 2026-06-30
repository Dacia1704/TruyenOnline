package com.dacia1704.truyenonline.module.chapter.mapper;

import com.dacia1704.truyenonline.module.chapter.dto.request.ChapterPageRequest;
import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterPageResponse;
import com.dacia1704.truyenonline.module.chapter.entity.ChapterPage;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ChapterPageMapper {
    ChapterPageResponse toChapterPageResponse(ChapterPage chapterPage);

    ChapterPage toChapter(ChapterPageRequest request);

    void updateChapterPage(@MappingTarget ChapterPage chapterPage, ChapterPageRequest request);
}
