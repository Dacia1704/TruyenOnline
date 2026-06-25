package com.dacia1704.truyenonline.module.interaction.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkId implements Serializable {
    private String user;
    private String story;
}