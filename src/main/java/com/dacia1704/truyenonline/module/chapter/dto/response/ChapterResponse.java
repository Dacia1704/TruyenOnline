package com.dacia1704.truyenonline.module.chapter.dto.response;

import com.dacia1704.truyenonline.module.story.dto.response.StoryResponse;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterResponse {
    String id;
    StoryResponse story;
    BigDecimal chapterNumber;
    String title;
    Boolean isPublished;
    Long viewCount = 0L;
    String content;
    Integer pageCount;
    List<ChapterPageResponse> pages;
    Boolean isBanned;
}
