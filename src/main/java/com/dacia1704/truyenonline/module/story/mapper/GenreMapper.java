package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.request.GenreRequest;
import com.dacia1704.truyenonline.module.story.dto.response.GenreResponse;
import com.dacia1704.truyenonline.module.story.entity.Genre;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface GenreMapper {
    Genre toGenre(GenreRequest request);

    @Mapping(
            target = "storyQuantity",
            expression = "java(genre.getStories() != null ? genre.getStories().size() : 0)")
    GenreResponse toGenreResponse(Genre genre);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateGenre(@MappingTarget Genre genre, GenreRequest request);
}
