package com.dacia1704.truyenonline.module.story.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "story_publish_requests")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StoryPublishRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    // Mối quan hệ N-1 với bảng Story (Nhiều request có thể thuộc về 1 truyện - lịch sử xin duyệt)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "story_id", nullable = false)
    Story story;

    @Column(name = "requester_note", columnDefinition = "TEXT")
    String requesterNote;

    @Column(name = "reviewer_note", columnDefinition = "TEXT")
    String reviewerNote;

    // Mối quan hệ N-1 với bảng User (Admin/Mod nào đã duyệt)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewer_id")
    User reviewer;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    StoryPublishRequestStatus status = StoryPublishRequestStatus.PENDING;
}
