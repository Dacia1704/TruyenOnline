package com.dacia1704.truyenonline.module.chapter.dto.request;

import com.dacia1704.truyenonline.module.chapter.entity.Chapter;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterPageListRequest {
    List<ChapterPageRequest> chapterPageRequests;
    String chapterId;
}
