package com.dacia1704.truyenonline.module.story.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors", indexes = {
        @Index(name = "idx_author_user_id", columnList = "user_id"),
        @Index(name = "idx_author_slug",    columnList = "slug")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author extends BaseEntity {

    @Id
    @Column(name = "id", columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "name_no_accent", nullable = false, length = 255)
    private String nameNoAccent;

    @Column(name = "slug", unique = true, nullable = false, length = 255)
    private String slug;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "country", length = 100)
    private String country;

    // Tuỳ chọn — NULL nếu tác giả chưa có tài khoản trên hệ thống
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_author_user"))
    private User user;

    // Danh sách truyện của tác giả (truy cập qua bảng trung gian)
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StoryAuthor> storyAuthors = new ArrayList<>();
}