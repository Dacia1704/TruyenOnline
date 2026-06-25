package com.dacia1704.truyenonline.module.chapter.entity;


import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(
        name = "chapter_pages",
        indexes = {
                @Index(name = "idx_page_chapter_id", columnList = "chapter_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_page", columnNames = {"chapter_id", "page_number"})
        })
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChapterPage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    // Mối quan hệ N-1 với bảng Chapter (Nhiều trang ảnh thuộc về 1 chapter)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    Chapter chapter;

    @Column(name = "page_number",nullable = false)
    Integer pageNumber;

    @Column(name = "image_url", length = 500,nullable = false)
    String imageUrl;

    @Column(name = "cloudinary_id", length = 200)
    String cloudinaryId;

    @Column(name = "width")
    Integer width;

    @Column(name = "height")
    Integer height;
}
