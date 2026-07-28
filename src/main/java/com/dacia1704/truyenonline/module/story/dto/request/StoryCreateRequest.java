package com.dacia1704.truyenonline.module.story.dto.request;

import com.dacia1704.truyenonline.module.story.entity.StoryStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryCreateRequest {
    @NotEmpty(message = "Trường tiêu đề không được để trống")
    String title;

    String description;

    String coverImageUrl;

    MultipartFile coverImageFile;

    @NotNull(message = "Trường thể loại truyện không được để trống")
    StoryType storyType;

    @Builder.Default
    StoryStatus status = StoryStatus.ONGOING;

    Integer freeChapterLimit;

    List<StoryAuthorUpdateRequest> authors;

}
