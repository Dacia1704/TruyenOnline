package com.dacia1704.truyenonline.module.story.mapper;

import com.dacia1704.truyenonline.module.story.dto.request.AuthorCreateRequest;
import com.dacia1704.truyenonline.module.story.dto.request.AuthorUpdateRequest;
import com.dacia1704.truyenonline.module.story.dto.response.AuthorResponse;
import com.dacia1704.truyenonline.module.story.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorMapper {

    @Mapping(target = "avatarUrl", ignore = true)
    Author toAuthor(AuthorCreateRequest request);

    AuthorResponse toAuthorResponse(Author author);

    @Mapping(target = "avatarUrl", ignore = true)
    void updateAuthor(@MappingTarget Author author, AuthorUpdateRequest request);
}