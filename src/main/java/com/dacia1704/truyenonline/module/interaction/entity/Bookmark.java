package com.dacia1704.truyenonline.module.interaction.entity;

import com.dacia1704.truyenonline.module.story.entity.Story;
import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "bookmarks")
@IdClass(BookmarkId.class)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Bookmark extends BaseEntity {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, columnDefinition = "CHAR(36)")
    @OnDelete(action = OnDeleteAction.CASCADE)
    User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false, columnDefinition = "CHAR(36)")
    @OnDelete(action = OnDeleteAction.CASCADE)
    Story story;
}
