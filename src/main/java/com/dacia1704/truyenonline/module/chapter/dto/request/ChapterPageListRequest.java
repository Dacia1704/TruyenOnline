package com.dacia1704.truyenonline.module.chapter.dto.request;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterPageListRequest {
    List<ChapterPageRequest> chapterPageRequests;
    List<MultipartFile> files;

    String chapterId;
}
