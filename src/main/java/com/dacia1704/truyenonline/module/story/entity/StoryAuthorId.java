package com.dacia1704.truyenonline.module.story.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StoryAuthorId implements Serializable {

    @Column(name = "story_id", columnDefinition = "CHAR(36)") // ← thêm
    private String storyId;

    @Column(name = "author_id", columnDefinition = "CHAR(36)") // ← thêm
    private String authorId;
}
