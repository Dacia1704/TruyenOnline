package com.dacia1704.truyenonline.module.chapter.dto.request;

import java.math.BigDecimal;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterUpdateRequest {
    BigDecimal chapterNumber;
    String title;
    boolean isPublished = false;
}
