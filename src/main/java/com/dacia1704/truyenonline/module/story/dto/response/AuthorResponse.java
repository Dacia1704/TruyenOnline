package com.dacia1704.truyenonline.module.story.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorResponse {
    String id;
    String name;
    String nameNoAccent;
    String slug;
    String bio;
    String avatarUrl;
    String country;
}
