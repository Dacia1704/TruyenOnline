package com.dacia1704.truyenonline.module.story.dto.request;

import com.dacia1704.truyenonline.module.story.entity.AuthorRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryAuthorUpdateRequest {
    @NotBlank(message = "Author ID không được để trống")
    String authorId;

    @NotNull(message = "Role không được để trống")
    AuthorRole role; // AUTHOR, CO_AUTHOR, ILLUSTRATOR, TRANSLATOR

    @Builder.Default
    Byte sortOrder = 1;
}