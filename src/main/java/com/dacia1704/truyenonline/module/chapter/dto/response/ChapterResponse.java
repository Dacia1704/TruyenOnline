package com.dacia1704.truyenonline.module.chapter.dto.response;

import com.dacia1704.truyenonline.module.story.entity.Story;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {
    String id;
    Story story;
    BigDecimal chapterNumber;
    String title;
    boolean isPublished;
    Long viewCount = 0L;
    String content;
    Integer pageCount;
}
