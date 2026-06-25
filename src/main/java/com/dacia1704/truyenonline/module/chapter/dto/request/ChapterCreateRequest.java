package com.dacia1704.truyenonline.module.chapter.dto.request;

import com.dacia1704.truyenonline.module.story.entity.Story;
import jakarta.persistence.*;
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
public class ChapterCreateRequest {
    BigDecimal chapterNumber;
    String title;
    boolean isPublished = false;
    Integer pageCount;

    String content;

    List<MultipartFile> pages;
}
