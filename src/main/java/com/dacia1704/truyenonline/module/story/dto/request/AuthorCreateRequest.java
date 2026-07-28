package com.dacia1704.truyenonline.module.story.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorCreateRequest {
    @NotBlank(message = "Tên tác giả không được để trống")
    String name;

    String bio;
    String country;

    MultipartFile avatarFile;
}
