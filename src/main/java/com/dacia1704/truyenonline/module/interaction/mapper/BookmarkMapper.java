package com.dacia1704.truyenonline.module.interaction.mapper;

import com.dacia1704.truyenonline.module.chapter.mapper.ChapterMapper;
import com.dacia1704.truyenonline.module.interaction.dto.response.BookmarkResponse;
import com.dacia1704.truyenonline.module.interaction.entity.Bookmark;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import com.dacia1704.truyenonline.module.user.dto.response.UserResponse;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import org.mapstruct.Mapper;

@Mapper(
        componentModel = "spring",
        uses = {
                StoryMapper.class,
                UserMapper.class
        }
)
public interface BookmarkMapper {
    BookmarkResponse toBookmarkResponse(Bookmark bookmark);
}
