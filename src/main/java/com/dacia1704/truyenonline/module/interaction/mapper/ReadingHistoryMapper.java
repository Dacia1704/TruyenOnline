package com.dacia1704.truyenonline.module.interaction.mapper;

import com.dacia1704.truyenonline.module.chapter.dto.response.ChapterResponse;
import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.chapter.mapper.ChapterMapper;
import com.dacia1704.truyenonline.module.interaction.dto.response.ReadingHistoryResponse;
import com.dacia1704.truyenonline.module.interaction.entity.ReadingHistory;
import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.story.mapper.StoryMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        uses = {
                StoryMapper.class,
                ChapterMapper.class
        }
)
public interface ReadingHistoryMapper {
    ReadingHistoryResponse toReadingHistoryResponse(ReadingHistory readingHistory);
}
