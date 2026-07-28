package com.dacia1704.truyenonline.module.story.dto.request;

import com.dacia1704.truyenonline.module.story.entity.StoryStatus;
import com.dacia1704.truyenonline.module.story.entity.StoryType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryFilter {
    public enum SortType {
        NEWEST,         // Mới tạo
        UPDATED,        // Mới cập nhật chapter
        VIEW,           // Lượt xem
        FOLLOW,         // Lượt theo dõi bookmark
        ALPHABET_ASC,   // A -> Z
        ALPHABET_DESC,  // Z -> A
        OLDEST          // Cũ nhất
    }

    String search;
    String uploaderId;
    StoryType type;
    StoryStatus status;
    Boolean isPublished;
    SortType sortType;
    List<String> genres; // có 1 trong các genre là dc, danh sách slug
    List<String> authors; // có 1 là dc, danh sách slug
}
