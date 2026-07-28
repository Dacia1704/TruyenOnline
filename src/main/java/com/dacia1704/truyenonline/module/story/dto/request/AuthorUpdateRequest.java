package com.dacia1704.truyenonline.module.story.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorUpdateRequest {
    String name;
    String bio;
    String country;
    MultipartFile avatarFile;
}