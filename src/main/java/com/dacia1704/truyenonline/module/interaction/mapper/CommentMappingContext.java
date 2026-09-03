package com.dacia1704.truyenonline.module.interaction.mapper;

public record CommentMappingContext(
        String currentUserId, boolean isAdmin, boolean isStoryUploader) {}
