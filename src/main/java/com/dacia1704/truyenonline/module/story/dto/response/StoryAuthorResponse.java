package com.dacia1704.truyenonline.module.story.dto.response;

import com.dacia1704.truyenonline.module.story.entity.AuthorRole;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryAuthorResponse {
    AuthorResponse author;
    StoryResponse story;
    AuthorRole role;
    Byte sortOrder = 1;
}
