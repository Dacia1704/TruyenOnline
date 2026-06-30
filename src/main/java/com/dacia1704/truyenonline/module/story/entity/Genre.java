package com.dacia1704.truyenonline.module.story.entity;

import com.dacia1704.truyenonline.shared.entity.BaseEntity;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "genres")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Genre extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    String name;

    @Column(name = "name_no_accent", nullable = false, unique = true, length = 50)
    String nameNoAccent;

    @Column(name = "slug", nullable = false, unique = true, length = 50)
    String slug;

    @ManyToMany(mappedBy = "genres")
    Set<Story> stories = new HashSet<>();
}
