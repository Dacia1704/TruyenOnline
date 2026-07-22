package com.dacia1704.truyenonline.module.interaction.dto.request;

import com.dacia1704.truyenonline.module.interaction.entity.CommentType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CommentCreateRequest {
    CommentType type;
    String storyId;
    String chapterId;
    String content;
    String parentId;
}
