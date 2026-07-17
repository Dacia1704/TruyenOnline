package com.dacia1704.truyenonline.module.chapter.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterUpdatePublishStatusRequest {
    List<String> chapterIdList;
    Boolean publishStatus = true;
}
