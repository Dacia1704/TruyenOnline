package com.dacia1704.truyenonline.module.chapter.dto.request;

import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;

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
