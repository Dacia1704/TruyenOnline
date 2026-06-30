package com.dacia1704.truyenonline.module.interaction.dto.request;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    String chapterId;
    String content;
    String parentId;
}
