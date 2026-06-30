package com.dacia1704.truyenonline.module.chapter.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterPageRequest {
    String chapterId;
    String id;
    Integer pageNumber;
    String imageUrl;
    MultipartFile file;
    Boolean isNewPage;
    Integer width;
    Integer height;
}
