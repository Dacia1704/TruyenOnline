package com.dacia1704.truyenonline.module.chapter.dto.response;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterPageResponse {
    String id;
    Integer pageNumber;
    String imageUrl;
    Integer width;
    Integer height;
}
