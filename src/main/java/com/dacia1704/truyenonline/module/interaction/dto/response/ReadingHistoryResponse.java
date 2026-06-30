package com.dacia1704.truyenonline.module.interaction.dto.response;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import com.dacia1704.truyenonline.module.story.entity.Story;
import java.time.LocalDateTime;

public class ReadingHistoryResponse {
    String id;
    Story story;
    Chapter chapter;
    LocalDateTime lastReadAt;
}
