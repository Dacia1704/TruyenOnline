package com.dacia1704.truyenonline.module.user.entity;

import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(
        name = "permissions",
        indexes = {@Index(name = "idx_perm_resource", columnList = "resource")})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Permission extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Dùng AUTO_INCREMENT
    @Column(name = "id")
    Integer id; // Đổi từ String thành Integer

    @Column(name = "name", length = 100, unique = true, nullable = false)
    String name;

    @Column(name = "resource", length = 50, nullable = false)
    String resource;

    @Column(name = "action", length = 50, nullable = false)
    String action;

    @Column(name = "description")
    String description;
}
