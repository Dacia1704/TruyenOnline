package com.dacia1704.truyenonline.module.administration.entity;

import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "ban_appeal_attachments",
        indexes = {@Index(name = "idx_appeal_attachment_appeal", columnList = "appeal_id")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BanAppealAttachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "CHAR(36)")
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appeal_id", nullable = false)
    BanAppeal appeal;

    @Column(name = "attachment_url", nullable = false, length = 500)
    String attachmentUrl;
}
