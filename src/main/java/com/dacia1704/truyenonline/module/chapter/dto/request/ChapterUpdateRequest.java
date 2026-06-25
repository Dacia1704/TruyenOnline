package com.dacia1704.truyenonline.module.chapter.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

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
    String content;
    Integer pageCount;

    List<MultipartFile> pages;
}
