package com.dacia1704.truyenonline.module.media.entity;

import com.dacia1704.truyenonline.module.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Đại diện cho một file đã upload lên Cloudinary. Story/Chapter/User...
 * tham chiếu tới entity này (qua id) thay vì lưu thẳng URL.
 *
 * Cơ chế reference_count (thay cho is_active):
 * - Upload mới          -> reference_count = 0
 * - Entity gắn file này -> reference_count += 1 (increment())
 * - Đổi ảnh / xoá entity -> reference_count -= 1 (decrement())
 * - reference_count = 0 -> file "mồ côi", chưa xoá ngay.
 *
 * Cron job định kỳ tìm các row có reference_count = 0 VÀ
 * updated_at đã quá X ngày (query trực tiếp trên bảng này,
 * không cần quét Story/Chapter/User) để xoá trên Cloudinary
 * rồi xoá bản ghi.
 */
@Entity
@Table(name = "media_files")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "CHAR(36)")
    String id;

    @Column(name = "public_id", length = 255, nullable = false, unique = true)
    private String publicId;

    @Column(name = "secure_url", length = 500, nullable = false, unique = true)
    private String secureUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", foreignKey = @ForeignKey(name = "fk_mf_uploaded_by"))
    private User uploadedBy;

    @Column(name = "reference_count", nullable = false)
    @Builder.Default
    private Integer referenceCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Gọi khi có entity mới bắt đầu sử dụng file này. */
    public void increment() {
        this.referenceCount = this.referenceCount + 1;
    }

    /** Gọi khi entity ngừng sử dụng file này (đổi ảnh / xoá entity). */
    public void decrement() {
        this.referenceCount = Math.max(0, this.referenceCount - 1);
    }

    @Transient
    public boolean isOrphan() {
        return referenceCount != null && referenceCount == 0;
    }
}