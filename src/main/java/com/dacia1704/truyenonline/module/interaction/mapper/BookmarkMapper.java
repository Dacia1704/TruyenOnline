package com.dacia1704.truyenonline.module.interaction.mapper;

import com.dacia1704.truyenonline.module.interaction.dto.response.BookmarkResponse;
import com.dacia1704.truyenonline.module.interaction.entity.Bookmark;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BookmarkMapper {
    BookmarkResponse toBookmarkResponse(Bookmark bookmark);
}
