package com.dacia1704.truyenonline.module.interaction.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkId implements Serializable {
    private String user;
    private String story;
}
